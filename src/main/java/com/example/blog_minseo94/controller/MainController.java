package com.example.blog_minseo94.controller;

import com.example.blog_minseo94.entity.MainEntity;
import com.example.blog_minseo94.entity.PostsEntity;
import com.example.blog_minseo94.service.BlogCommentService;
import com.example.blog_minseo94.service.MainService;
import com.example.blog_minseo94.service.PostsService;
import com.example.blog_minseo94.util.ImageExtractor;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;
    private final PostsService postsService;
    private final BlogCommentService blogCommentService;

    @GetMapping("/")
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

    @GetMapping("/join")
    public String join() {
        return "join"; // 회원가입 페이지 반환
    }

    @PostMapping("/joinproc")
    public String joinProc(@ModelAttribute MainEntity mainEntity) {
        //나이계산 서비스로 뺏음
        mainService.saveUser(mainEntity); // 서비스 호출하여 사용자 저장
        return "redirect:/joinok"; // 회원가입 완료 페이지로 리다이렉트
    }

    @GetMapping("/joinok")
    public String joinok() {
        return "joinok"; // 로그인 페이지 반환
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // 로그인 페이지 반환
    }

    @PostMapping("/loginproc")
    public String loginProc(@RequestParam("id") String id,
                            @RequestParam("pwd") String pwd,
                            HttpSession session, Model model) {
        MainEntity user = mainService.findUserById(id); // 서비스 호출하여 사용자 찾기

        // 사용자가 없을 경우
        if (user == null) {
            model.addAttribute("errorMessage", "아이디를 찾을 수 없습니다.");
            return "login"; // 로그인 실패 시 다시 로그인 페이지로 이동
        }

        // 비밀번호가 일치할 경우
        if (user.getPwd().equals(pwd)) {
            session.setAttribute("loggedInUserId", user.getId());
            session.setAttribute("loggedInUserNickname", user.getNickname());
            session.setAttribute("loggedInUserIdx", user.getIdx());
            return "redirect:/posts_trend"; // 로그인 성공 후 메인 페이지로 이동
        } else {
            // 비밀번호가 틀릴 경우
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "login"; // 로그인 실패 시 다시 로그인 페이지로 이동
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/"; // 로그아웃 후 메인 페이지로 이동
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        // 세션에서 로그인한 사용자 ID 가져오기
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        // 만약 세션에 사용자 정보가 없다면 로그인 페이지로 리다이렉트
        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        // 로그인한 사용자의 정보 찾기
        MainEntity user = mainService.findUserById(loggedInUserId);

        // 사용자 정보를 모델에 담아서 뷰로 전달
        model.addAttribute("user", user);
        return "profile"; // profile.html 템플릿 반환
    }

    // 비밀번호 확인 페이지 요청
    @GetMapping("/pwd-check")
    public String showPwdCheckPage() {
        return "pwd-check"; // 비밀번호 확인 페이지 반환
    }

    // 비밀번호 확인 처리
    @PostMapping("/pwd-check")
    public String checkPassword(@RequestParam("pwd") String pwd, HttpSession session, Model model) {
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        // 서비스에서 비밀번호 확인
        if (mainService.checkPassword(loggedInUserId, pwd)) {
            return "redirect:/modify"; // 비밀번호가 맞으면 수정 페이지로 이동
        } else {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "pwd-check"; // 비밀번호가 틀리면 다시 비밀번호 확인 페이지로 이동
        }

    }

    // 개인정보 수정 페이지
    @GetMapping("/modify")
    public String showModifyPage(HttpSession session, Model model) {
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        MainEntity user = mainService.findUserById(loggedInUserId);
        model.addAttribute("user", user); // 사용자 정보를 모델에 추가

        return "modify"; // 개인정보 수정 페이지 반환
    }

    // 개인정보 수정 처리
    @PostMapping("/modifyproc")
    public String modifyUser(@ModelAttribute MainEntity modifiedUser, HttpSession session) {
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        }

        // 서비스에서 수정 로직 처리
        mainService.updateUser(loggedInUserId, modifiedUser);

        return "redirect:/profile"; // 수정 완료 후 프로필 페이지로 리다이렉트
    }

}
