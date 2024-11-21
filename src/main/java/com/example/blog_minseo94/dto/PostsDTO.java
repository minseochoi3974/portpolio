package com.example.blog_minseo94.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PostsDTO {
    private Long idx; // 게시물 고유 ID
    private String title; // 게시물 제목
    private String content; // 게시물 내용
    private String nickname; //게시물 닉네임
    private LocalDateTime createdDate;
    private Long usersIdx; // 작성자의 users 테이블 외래키
    private String visibility; // 공개 범위 (public 또는 secret)
    private Long views; // 조회수 필드
    private Long likes; // 좋아요 수

}
