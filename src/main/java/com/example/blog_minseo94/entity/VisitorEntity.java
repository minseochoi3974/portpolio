package com.example.blog_minseo94.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "visitor")
@Data
@NoArgsConstructor
public class VisitorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키, 자동 증가

    @Column(name = "visited_date", nullable = false)
    private LocalDate visitedDate;  // 방문 날짜, 기본값은 현재 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id", nullable = false)  // 방문자 외래 키
    private MainEntity viewer;  // 방문자 정보 (현재 로그인한 유저)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visited_user_id", nullable = false)  // 방문받은 게시글 작성자 외래 키
    private MainEntity visitedUser;  // 방문받은 유저 정보 (게시글 작성자)
}