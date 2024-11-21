package com.example.blog_minseo94.controller;

import com.example.blog_minseo94.dto.BlogCommentDTO;
import com.example.blog_minseo94.dto.VisitorDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Paths;
import java.net.MalformedURLException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
public class PostsController {

    private final MainService mainService;
    private final PostsService postsService;
    private final BlogCommentService blogcommentService;
    private final VisitorService visitorService;


    @GetMapping("/myblog")
    public String myblog(HttpSession session, Model model) {

        String loggedInUserId = (String) session.getAttribute("loggedInUserId");
        // 세션에서 사용자 idx 가져오기
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        // 로그인 확인
        if (loggedInUserId == null) {
            return "redirect:/login"; // 로그인 안된 경우 로그인 페이지로 리다이렉트
        }

        // 블로그 설정 가져오기
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(loggedInUserIdx);

        if (blogSettings != null) {
            // 블로그 설정 값 가져오기
            model.addAttribute("profileImage", blogSettings.getBlogImage());
            model.addAttribute("nickname", blogSettings.getBlogNickname());
            model.addAttribute("bio", blogSettings.getBlogMessage());
            model.addAttribute("blogTitle", blogSettings.getBlogTitle());
            model.addAttribute("layout", blogSettings.getLayout());
        } else {
            // 기본값 설정
            model.addAttribute("profileImage", "/setting_img/noimge.jpg");  // 기본 프로필 이미지
            model.addAttribute("nickname", "닉네임");
            model.addAttribute("bio", "소개");
            model.addAttribute("blogTitle", "Blog");
            model.addAttribute("layout", 1); // 기본 레이아웃 1
        }

        // 방문자 서비스에서 방문자 관련 데이터 가져오기
        List<VisitorDTO> recentVisitors = visitorService.getRecentVisitors(loggedInUserIdx);
        int dailyVisitorCount = visitorService.getTodayVisitorCount(loggedInUserIdx);
        int totalVisitorCount = visitorService.getTotalVisitorCount(loggedInUserIdx);

        model.addAttribute("recentVisitors", recentVisitors);      // 최근 방문자 목록
        model.addAttribute("dailyVisitorCount", dailyVisitorCount); // 오늘 방문자 수
        model.addAttribute("totalVisitorCount", totalVisitorCount); // 총 방문자 수

        System.out.println("Today Visitor Count: " + dailyVisitorCount);
        System.out.println("Total Visitor Count: " + totalVisitorCount);
        System.out.println("Recent Visitors: " + recentVisitors); // 로그 추가

        // 최신 게시물 3개 가져오기
        //postsservice 메서드 호출하여 로그인한 사용자의 id를 기준으로 최신 게시물을 3개 가져옴
        //recentposts 리스트에 postsentity 객체(게시물의 정보를 담고있음) 3개가 저장 됨
        //findTop3ByUserIdxOrderByCreatedDateDesc 메서드는 해당 사용자가 작성한 게시물 중에서 생성일 기준으로 최신 3개를 가져 옴
        List<PostsEntity> recentPosts = postsService.getTop3Posts(loggedInUserIdx);

        // 이미지 URL 추출
        List<String> imageUrls = recentPosts.stream()
                .flatMap(post -> ImageExtractor.extractImageUrls(post.getContent()).stream())
                .collect(Collectors.toList());

        model.addAttribute("recentPosts", recentPosts); // 모델에 최신 게시물 추가
        model.addAttribute("imageUrls", imageUrls); // 모델에 이미지 URL 추가


        // 인기 게시물 3개 가져오기
        List<PostsEntity> popularPosts = postsService.getTop3PopularPosts(loggedInUserIdx);

// 각 게시물에 대해 이미지 URL 추출 (이미지 없을 경우 기본 이미지로 처리)
        List<String> popularImageUrls = popularPosts.stream()
                .map(post -> {
                    // 게시물 내용에서 이미지 URL 추출
                    List<String> images = ImageExtractor.extractImageUrls(post.getContent());
                    // 이미지가 없는 경우 기본 이미지 경로 반환
                    return images.isEmpty() ? "/images/default_image.png" : images.get(0);
                })
                .collect(Collectors.toList());

// Thymeleaf로 데이터 전달
        model.addAttribute("popularPosts", popularPosts);
        model.addAttribute("popularImageUrls", popularImageUrls); // 인기 게시물 이미지 URL 리스트

        return "myblog"; // myblog.html 반환
    }

    //블로그 수정
    @GetMapping("/edit")
    public String edit(HttpSession session, Model model) {
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        // 로그인 확인
        if (loggedInUserIdx == null) {
            return "redirect:/login"; // 로그인 안되어있으면 로그인 페이지로 리다이렉트
        }

        // 로그인한 사용자의 블로그 설정 가져오기
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(loggedInUserIdx);
        if (blogSettings != null) {
            model.addAttribute("profileImage", blogSettings.getBlogImage());
            model.addAttribute("nickname", blogSettings.getBlogNickname());
            model.addAttribute("bio", blogSettings.getBlogMessage());
            model.addAttribute("blogTitle", blogSettings.getBlogTitle());
            model.addAttribute("layout", blogSettings.getLayout());
        } else {
            // 기본값 설정
            model.addAttribute("profileImage", "/setting_img/noimge.jpg");  // 기본 프로필 이미지
            model.addAttribute("nickname", "닉네임");
            model.addAttribute("bio", "소개");
            model.addAttribute("blogTitle", "Blog");
            model.addAttribute("layout", 1); // 기본 레이아웃 1
        }

        return "edit";
    }

    //프로필 이미지, 소개, 닉네임 설정
    @PostMapping("/saveProfileSettings")
    public String saveProfileSettings(
            @RequestParam("nickname") String nickname,
            @RequestParam("bio") String bio,
            @RequestParam("profileImage") MultipartFile profileImage,
            HttpSession session, Model model) {

        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        // 블로그 설정 가져오기 (없으면 새로 생성)
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(loggedInUserIdx);
        if (blogSettings == null) {
            blogSettings = new BlogSettingsEntity();
            blogSettings.setUserIdx(loggedInUserIdx); // 외래키 설정
        }

        // 프로필 이미지 처리
        if (!profileImage.isEmpty()) {
            String fileName = profileImage.getOriginalFilename();
            String imagePath = "D:/blog_minseo94/src/main/resources/static/setting_img/" + fileName;
            File destFile = new File(imagePath);
            try {
                profileImage.transferTo(destFile);
                blogSettings.setBlogImage("/setting_img/" + fileName); // 이미지 경로 설정
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 닉네임 및 한줄 설명 저장
        blogSettings.setBlogNickname(nickname);
        blogSettings.setBlogMessage(bio);

        model.addAttribute("profileImage", blogSettings.getBlogImage());

        // 블로그 설정 저장
        postsService.updateBlogSettings(blogSettings);

        return "redirect:/edit?success"; // 성공 후 다시 edit 페이지로 이동
    }

    // 블로그 제목 설정
    @PostMapping("/saveBlogTitle")
    public String saveBlogTitle(
            @RequestParam(value = "blogTitle", required = false) String blogTitle,
            HttpSession session) {

        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        // 블로그 설정 가져오기 (없으면 새로 생성)
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(loggedInUserIdx);
        if (blogSettings == null) {
            blogSettings = new BlogSettingsEntity();
            blogSettings.setUserIdx(loggedInUserIdx); // 외래키 설정
        }

        // 블로그 제목 업데이트
        if (blogTitle != null && !blogTitle.isEmpty()) {
            blogSettings.setBlogTitle(blogTitle);
        }

        // 블로그 설정 저장
        postsService.updateBlogSettings(blogSettings);

        return "redirect:/edit?success"; // 성공 후 다시 edit 페이지로 이동
    }


    // 블로그 레이아웃 설정
    @PostMapping("/saveBlogSettings")
    public String saveBlogSettings(@RequestParam("layout") Integer layout, HttpSession session) {
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        // 블로그 설정 가져오기 (없으면 새로 생성)
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(loggedInUserIdx);
        if (blogSettings == null) {
            blogSettings = new BlogSettingsEntity();
            blogSettings.setUserIdx(loggedInUserIdx); // 외래키 설정
        }

        // 레이아웃 설정
        blogSettings.setLayout(layout); // 값이 있는 경우 설정

        // 블로그 설정 저장
        postsService.updateBlogSettings(blogSettings);

        return "redirect:/edit?success"; // 성공 후 다시 edit 페이지로 이동
    }

    //게시글 작성
    @GetMapping("/blog_text")
    public String blog_text(HttpSession session, Model model) {
        // 현재 로그인한 사용자의 닉네임 가져오기
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login"; // 로그인이 안 되어 있으면 로그인 페이지로 리다이렉트
        }

        MainEntity loggedInUser = mainService.findUserById(loggedInUserId);
        String nickname = loggedInUser.getNickname(); // 닉네임 가져오기

        // 세션에서 사용자 idx 가져오기
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        // 블로그 제목 가져오기
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(loggedInUserIdx);

        if (blogSettings != null) {
            // 블로그 설정 값 가져오기
            model.addAttribute("blogTitle", blogSettings.getBlogTitle());  // 블로그 제목 추가
        } else {
            // 기본값 설정
            model.addAttribute("blogTitle", "블로그 제목 없음");  // 기본 블로그 제목 설정
        }

        model.addAttribute("nickname", nickname); // 닉네임을 모델에 추가

        return "blog_text"; // 게시글 작성 폼으로 이동
    }

    //게시물 작성 등록 컨트롤러
    //써머노트 내 이미지 첨부는 따로 컨트롤러 뺌
    @PostMapping("/submitPost")
    public String submitPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("nickname") String nickname,
            @RequestParam("file_attachment") MultipartFile[] fileAttachments,
            @RequestParam(value = "visibility", defaultValue = "PUBLIC") String visibility,
            HttpSession session) throws IOException {

        // 현재 로그인한 사용자 확인
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login"; // 로그인 안된 상태일 경우 로그인 페이지로 이동
        }

        MainEntity user = mainService.findUserById(loggedInUserId);

        // 게시글 저장 로직
        PostsEntity post = new PostsEntity();
        post.setTitle(title);
        post.setContent(content); // Summernote 에디터로 작성된 내용 저장
        post.setNickname(nickname); // 닉네임은 수정 불가
        post.setCreatedDate(LocalDateTime.now());
        post.setUser(user); // 작성자 정보 설정
        post.setVisibility(visibility); // 공개 범위 설정
        postsService.savePost(post); // 서비스에서 게시글 저장

        for (MultipartFile file : fileAttachments) {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename(); // 파일 이름 저장
                String filePath = saveFile(file, "D:/blog_minseo94/src/main/resources/static/blog_file/");
                postsService.saveAttachment(post, filePath, "file", fileName); // 파일 이름도 함께 저장
            }
        }

        return "redirect:/myblog/postlist"; // 게시글 등록 후 마이 블로그 페이지로 이동
    }

    // 파일 저장 로직
    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        String fileName = file.getOriginalFilename();
        File destination = new File(uploadDir + fileName);
        file.transferTo(destination);
        return "/static/" + fileName; // 파일 경로 반환
    }

    //서머노트 이미지 첨부 업로드 용도 컨트롤러
    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }

        try {
            // 이미지 파일 저장 경로 설정
            String uploadDir = "D:/blog_minseo94/src/main/resources/static/blog_img/";
            String fileName = file.getOriginalFilename();
            File destination = new File(uploadDir + fileName);

            // 파일을 해당 경로로 저장
            file.transferTo(destination);

            // 성공적으로 저장된 파일 경로 반환
            String imageUrl = "/blog_img/" + fileName;
            return new ResponseEntity<>(imageUrl, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시물 리스트
    @GetMapping("/myblog/postlist")
    public String postlist(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "15") int size,
            Model model, HttpSession session) {

        // 로그인 확인
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");
        if (loggedInUserIdx == null) {
            return "redirect:/login";
        }

        // 사용자별 블로그 제목 가져오기
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(loggedInUserIdx);
        if (blogSettings != null) {
            model.addAttribute("blogTitle", blogSettings.getBlogTitle());
        } else {
            model.addAttribute("blogTitle", "Blog");
        }

        // 사용자에 맞는 게시물 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<PostsEntity> postsPage = postsService.findPostsByUserId(loggedInUserIdx, pageable); // 필터링된 게시글 조회

        model.addAttribute("posts", postsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postsPage.getTotalPages());
        model.addAttribute("size", size); // 페이지 크기 전달

        return "postlist";
    }

    //게시글 리스트 검색 기능
    @GetMapping("/search")
    public String searchPosts(@RequestParam("searchType") String searchType,
                              @RequestParam("keyword") String keyword,
                              Model model) {
        List<PostsEntity> searchResults;

        // 검색 타입에 따라 서비스에서 검색 처리
        if ("title".equals(searchType)) {
            searchResults = postsService.searchByTitle(keyword);
        } else if ("nickname".equals(searchType)) {
            searchResults = postsService.searchByNickname(keyword);
        } else {
            searchResults = new ArrayList<>();
        }

        model.addAttribute("posts", searchResults);
        return "postlist"; // 게시물 리스트 페이지로 이동
    }


    // 게시물 보기 및 조회수 증가, 댓글 가져오는 기능, 게시글 삭제/수정
    @GetMapping("/post/{idx}")
    public String viewPost(@PathVariable("idx") Long idx, Model model, HttpSession session) {
        // 게시물 찾기
        PostsEntity post = postsService.findPostById(idx); // idx로 게시물 검색
        if (post == null) {
            return "redirect:/postlist"; // 게시물이 없으면 리스트로 리다이렉트
        }

        // 로그인 확인
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");
        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        // **viewerId를 세션에서 가져온 loggedInUserIdx로 설정**
        Long viewerId = loggedInUserIdx;

        // 방문 기록 추가
        Long visitedUserId = post.getUser().getIdx(); // 게시글 작성자 ID
        visitorService.addVisitor(viewerId, visitedUserId);

        // 사용자가 해당 게시물을 처음 보는 경우에만 조회수 증가
        String viewKey = "viewedPost_" + idx; // 세션에 저장할 키


        // 사용자별 블로그 제목 가져오기
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(loggedInUserIdx);
        if (blogSettings != null) {
            model.addAttribute("blogTitle", blogSettings.getBlogTitle());
            model.addAttribute("profileImage", blogSettings.getBlogImage());
        } else {
            model.addAttribute("blogTitle", "블로그 제목 없음");
            model.addAttribute("profileImage", "/setting_img/noimge.jpg");
        }

        // 사용자가 해당 게시물을 처음 보는 경우에만 조회수 증가
        if (session.getAttribute(viewKey) == null) {
            // 세션에 해당 게시물을 본 적이 없다면 조회수 증가
            post.setViews(post.getViews() + 1);  // 조회수 증가
            postsService.savePost(post);         // 업데이트된 조회수 저장
            session.setAttribute(viewKey, true); // 조회수를 증가시켰음을 세션에 기록
        }

        // 작성자와 로그인한 사용자가 동일한지 확인하여 수정/삭제 버튼 표시
        if (post.getUser().getIdx().equals(loggedInUserIdx)) {
            model.addAttribute("isAuthor", true); // 작성자와 동일하면 true
        } else {
            model.addAttribute("isAuthor", false); // 작성자가 아니면 false
        }

        // 게시물 정보 모델에 추가
        model.addAttribute("post", post);
        model.addAttribute("loggedInUserId", loggedInUserId); // 로그인한 사용자 ID 추가
        model.addAttribute("loggedInUserIdx", loggedInUserIdx);

        // 댓글 가져오기 및 모델에 추가
        List<BlogCommentDTO> comment =  blogcommentService.getCommentsByPostId(idx);
        model.addAttribute("comments", comment);

        // **게시물의 첨부 파일 가져오기 및 모델에 추가**
        List<AttachmentsEntity> attachments = postsService.getAttachmentsByPostId(idx);  // 첨부 파일 리스트 가져오기
        model.addAttribute("attachments", attachments); // 모델에 추가

        return "post_detail"; // 게시물 상세보기 페이지로 이동
    }



    // 게시글 보기에서 파일 다운로드 가능하게 하는 메서드
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws MalformedURLException {
        // 파일 경로 지정 (실제 파일이 저장된 경로)
        String filePath = "D:/blog_minseo94/src/main/resources/static/blog_file/" + fileName;

        // 파일을 리소스로 읽어오기
        Resource resource = new UrlResource(Paths.get(filePath).toUri());

        // 파일이 존재하는지 확인
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 파일 다운로드를 위한 헤더 설정
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // 바이너리 파일로 지정
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"") // 다운로드 강제
                .body(resource);
    }

    //좋아요기능
    @PostMapping("/post/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable("postId") Long postId, HttpSession session) {
        String viewKey = "likedPost_" + postId;
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

        // 좋아요 수를 가져와서 JSON 형태로 반환
        Long updatedLikes = postsService.findPostById(postId).getLikes();
        Map<String, Object> response = new HashMap<>();
        response.put("likes", updatedLikes);
        response.put("isLiked", isLiked);

        return ResponseEntity.ok(response);
    }

    //댓글
    @PostMapping("/post/{postId}/comments")
    @ResponseBody // JSON 형태로 응답하기 위해 추가
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable("postId") Long postId,
            @RequestParam("content") String content,
            HttpSession session) {

        // 세션에서 사용자 정보 가져오기
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");

        if (loggedInUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // 로그인하지 않은 경우
        }

        // 사용자 정보 가져오기
        MainEntity user = mainService.findUserById(loggedInUserId);

        // 댓글 DTO 생성 및 저장
        BlogCommentDTO commentDTO = new BlogCommentDTO();
        commentDTO.setPostId(postId);
        commentDTO.setUserId(loggedInUserIdx);
        commentDTO.setNickname(user.getNickname());
        commentDTO.setContent(content);

        blogcommentService.addComment(commentDTO);

        // JSON 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("nickname", commentDTO.getNickname());
        response.put("content", commentDTO.getContent());
        response.put("createdDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        response.put("userId", loggedInUserIdx); // 댓글 작성자 ID 추가
        response.put("commentIdx", commentDTO.getCommentIdx()); // 새 댓글의 ID 추가

        return ResponseEntity.ok(response);
    }



    // 게시글 수정 페이지로 이동
    @GetMapping("/post/{id}/edit")
    public String editPost(@PathVariable("id") Long postId, Model model, HttpSession session) {
        // 게시글 정보 가져오기
        PostsEntity post = postsService.findPostById(postId);

        // 로그인된 사용자가 작성자인지 확인
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");
        if (!post.getUser().getIdx().equals(loggedInUserIdx)) {
            // 작성자가 아닌 경우 접근 불가 처리
            return "redirect:/post/" + postId; // 다시 게시물 보기 페이지로 리다이렉트
        }

        // 모델에 게시글 정보 추가
        model.addAttribute("post", post);
        model.addAttribute("nickname", post.getUser().getNickname());

        // 블로그 제목 추가
        BlogSettingsEntity blogSettings = postsService.findBlogSettingsByUserIdx(loggedInUserIdx);
        model.addAttribute("blogTitle", blogSettings != null ? blogSettings.getBlogTitle() : "My Blog");

        return "post_edit"; // 수정 페이지로 이동
    }

    // 게시글 수정 처리
    @PostMapping("/post/{id}/update")
    public String updatePost(@PathVariable("id") Long postId, @ModelAttribute PostsEntity postForm, HttpSession session) {
        // 게시글 찾기
        PostsEntity post = postsService.findPostById(postId);

        // 로그인된 사용자가 작성자인지 확인
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx");
        if (!post.getUser().getIdx().equals(loggedInUserIdx)) {
            return "redirect:/post/" + postId; // 작성자가 아닌 경우 접근 불가
        }

        // 게시글 정보 업데이트
        post.setTitle(postForm.getTitle());
        post.setContent(postForm.getContent());
        post.setVisibility(postForm.getVisibility());

        // 게시글 저장
        postsService.savePost(post);

        return "redirect:/post/" + postId; // 수정 후 게시글 보기 페이지로 이동
    }

    // 게시물 삭제 메소드
    @PostMapping("/post/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long id) {
        postsService.deletePost(id);  // 게시물 삭제 처리

        // 응답으로 리다이렉트 URL을 JSON 형태로 전송
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/myblog/postlist");
        return ResponseEntity.ok(response);
    }

    //댓글 수정 메소드
    @PostMapping("/comment/{commentId}/edit")
    @ResponseBody  // JSON 응답을 위해 추가
    public ResponseEntity<?> editComment(@PathVariable Long commentId,
                                         @RequestParam("content") String content,
                                         HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserIdx");
        if (loggedInUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            blogcommentService.updateComment(commentId, content, loggedInUserId);  // 본인 확인 후 수정
            return ResponseEntity.ok().build();  // 성공 시 빈 응답 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
    }


    //댓글 삭제
    @PostMapping("/comment/{commentIdx}/delete")
    @ResponseBody  // JSON 응답을 위해 추가
    public ResponseEntity<?> deleteComment(@PathVariable Long commentIdx, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserIdx");
        if (loggedInUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            blogcommentService.deleteComment(commentIdx, loggedInUserId);  // 본인 확인 후 삭제
            return ResponseEntity.ok().build();  // 성공 시 빈 응답 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
    }




}

