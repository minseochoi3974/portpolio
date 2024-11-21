package com.example.blog_minseo94.controller;

import com.example.blog_minseo94.entity.PostsEntity;
import com.example.blog_minseo94.service.PostsService;
import com.example.blog_minseo94.service.VisitorService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/visitor")
public class VisitorController {

    private final VisitorService visitorService;
    private final PostsService postsService; // 게시글 작성자 조회용 서비스 추가

    // 방문 기록 추가 (예: /visitor/add?postId=1)
    @PostMapping("/add")
    public void addVisitor(HttpSession session, @RequestParam Long postId) {
        Long viewerId = (Long) session.getAttribute("loggedInUserIdx"); // 세션에서 로그인 유저 ID 가져오기
        if (viewerId != null) {
            // 게시글 작성자 ID 조회
            PostsEntity post = postsService.findPostById(postId); // postId로 게시글 조회
            Long visitedUserId = post.getUser().getIdx(); // 게시글 작성자의 ID 추출
            visitorService.addVisitor(viewerId, visitedUserId); // 방문자와 게시글 작성자 정보로 방문 기록 추가
        }
    }

    // 방문자 수 조회
    @GetMapping("/count")
    public Map<String, Integer> getVisitorCounts(HttpSession session) {
        Long loggedInUserIdx = (Long) session.getAttribute("loggedInUserIdx"); // 세션에서 로그인 유저 ID 가져오기

        Map<String, Integer> counts = new HashMap<>();
        counts.put("today", visitorService.getTodayVisitorCount(loggedInUserIdx)); // 오늘 방문자 수
        counts.put("total", visitorService.getTotalVisitorCount(loggedInUserIdx)); // 전체 방문자 수

        return counts;
    }
}