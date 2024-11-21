package com.example.blog_minseo94.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class VisitorDTO {
    private Long id;                 // 방문 기록의 고유 ID
    private Long viewerId;           // 방문자 ID (users 테이블의 idx를 참조)
    private Long visitedUserId;      // 방문한 게시글 작성자 ID (users 테이블의 idx를 참조)
    private String nickname; // 방문자의 닉네임 필드 추가
    private LocalDate visitedDate;   // 방문 날짜
}
