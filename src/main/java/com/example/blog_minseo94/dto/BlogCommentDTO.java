package com.example.blog_minseo94.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogCommentDTO {
    private Long commentIdx;     // 댓글 ID
    private Long postId;         // 게시물 ID (posts 테이블과 연결)
    private Long userId;         // 작성자 ID (users 테이블과 연결)
    private String nickname;     // 작성자 닉네임
    private String content;      // 댓글 내용
    private LocalDateTime createdDate = LocalDateTime.now(); // 댓글 작성 날짜
}
