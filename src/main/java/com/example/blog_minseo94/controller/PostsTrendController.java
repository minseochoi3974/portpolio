package com.example.blog_minseo94.controller;

import com.example.blog_minseo94.dto.BlogCommentDTO;
import com.example.blog_minseo94.entity.AttachmentsEntity;
import com.example.blog_minseo94.entity.BlogSettingsEntity;
import com.example.blog_minseo94.entity.MainEntity;
import com.example.blog_minseo94.entity.PostsEntity;
import com.example.blog_minseo94.service.BlogCommentService;
import com.example.blog_minseo94.service.MainService;
import com.example.blog_minseo94.service.PostsService;
import com.example.blog_minseo94.service.VisitorService;
import com.example.blog_minseo94.util.ImageExtractor;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PostsTrendController {

    private final PostsService postsService;
    private final BlogCommentService blogCommentService;
    private final MainService mainService;
    private final VisitorService visitorService;

    @GetMapping("/posts_trend")
    public String showTrendPosts(Model model, HttpSession session) {
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        // 전체 공개 게시물 가져오기
        List<PostsEntity> trendingPosts = postsService.getTrendingPosts();
        List<String> trendingImageUrls = trendingPosts.stream()
                .flatMap(post -> ImageExtractor.extractImageUrls(post.getContent()).stream())
                .collect(Collectors.toList());

        Map<Long, Integer> commentCountMap = new HashMap<>();
        for (PostsEntity post : trendingPosts) {
            int commentCount = blogCommentService.countCommentsByPostId(post.getIdx());
            commentCountMap.put(post.getIdx(), commentCount);
        }

        model.addAttribute("trendingPosts", trendingPosts);
        model.addAttribute("trendingImageUrls", trendingImageUrls);
        model.addAttribute("commentCountMap", commentCountMap);
        model.addAttribute("loggedInUserIdx", loggedInUserIdx);

        return "posts_trend";
    }

    //게시글 진입
    @GetMapping("/posts_trend/post/{postId}")
    public String viewTrendPost(@PathVariable("postId") Long postId, Model model, HttpSession session) {
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        PostsEntity post = postsService.findPostById(postId);
        if (post == null) {
            return "error/404";
        }

        Long viewerId = (Long) session.getAttribute("loggedInUserIdx");
        if (viewerId != null) {
            Long visitedUserId = post.getUser().getIdx(); // 게시글 작성자 ID
            visitorService.addVisitor(viewerId, visitedUserId);
        }

        // 작성자 정보 가져오기
        MainEntity user = post.getUser();
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(user.getIdx());

        List<BlogCommentDTO> comments = blogCommentService.getCommentsByPostId(postId);
        List<AttachmentsEntity> attachments = postsService.getAttachmentsByPostId(postId);

        // 좋아요 및 댓글 작성자 여부 확인
        model.addAttribute("isAuthor", post.getUser().getIdx().equals(loggedInUserIdx));
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("attachments", attachments);
        model.addAttribute("blogTitle", blogSettings != null ? blogSettings.getBlogTitle() : "velog"); // 블로그 제목
        model.addAttribute("loggedInUserIdx", loggedInUserIdx);

        return "post_trend_detail";
    }

    @PostMapping("/posts_trend/{postId}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleLikeInTrend(@PathVariable("postId") Long postId, HttpSession session) {
        String viewKey = "likedPostTrend_" + postId;
        boolean isLiked;

        if (session.getAttribute(viewKey) == null) {
            postsService.incrementLikes(postId);
            session.setAttribute(viewKey, true);
            isLiked = true;
        } else {
            postsService.decrementLikes(postId);
            session.removeAttribute(viewKey);
            isLiked = false;
        }

        Long updatedLikes = postsService.findPostById(postId).getLikes();
        Map<String, Object> response = new HashMap<>();
        response.put("likes", updatedLikes);
        response.put("isLiked", isLiked);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts_trend/{postId}/comments")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCommentInTrend(
            @PathVariable("postId") Long postId,
            @RequestParam("content") String content,
            HttpSession session) {

        String loggedInUserId = (String) session.getAttribute("loggedInUserId");
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        if (loggedInUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MainEntity user = mainService.findUserById(loggedInUserId);

        BlogCommentDTO commentDTO = new BlogCommentDTO();
        commentDTO.setPostId(postId);
        commentDTO.setUserId(loggedInUserIdx);
        commentDTO.setNickname(user.getNickname());
        commentDTO.setContent(content);

        blogCommentService.addComment(commentDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("nickname", commentDTO.getNickname());
        response.put("content", commentDTO.getContent());
        response.put("createdDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        response.put("userId", loggedInUserIdx);
        response.put("commentIdx", commentDTO.getCommentIdx());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts_trend/download/{fileName}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String fileName) throws MalformedURLException {
        String filePath = "D:/blog_minseo94/src/main/resources/static/blog_file/" + fileName;
        Resource resource = new UrlResource(Paths.get(filePath).toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @PostMapping("/posts_trend/comment/{commentId}/edit")
    @ResponseBody
    public ResponseEntity<?> editComment(@PathVariable Long commentId,
                                         @RequestParam("content") String content,
                                         HttpSession session) {
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");
        if (loggedInUserIdx == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            blogCommentService.updateComment(commentId, content, loggedInUserIdx);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
    }

    @PostMapping("/posts_trend/comment/{commentIdx}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteComment(@PathVariable Long commentIdx, HttpSession session) {
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");
        if (loggedInUserIdx == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            blogCommentService.deleteComment(commentIdx, loggedInUserIdx);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
    }
}
