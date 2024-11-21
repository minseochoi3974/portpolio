package com.example.blog_minseo94.dto;

import lombok.Data;

@Data
public class BlogSettingsDTO {
    private Long userIdx; // 사용자 정보 (users 테이블과 연결)
    private String blogNickname; // 블로그 닉네임
    private String blogImage; // 프로필 이미지 경로
    private String blogMessage; // 상태 메시지
    private String blogTitle; // 블로그 이름
    private Integer  layout; // 블로그 레이아웃 정보
}
