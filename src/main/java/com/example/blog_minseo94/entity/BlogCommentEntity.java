package com.example.blog_minseo94.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "Blog_comments")
@Data
@ToString
public class BlogCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx")  // 테이블의 기본키에 맞게 수정
    private Long commentIdx;

    @ManyToOne
    @JoinColumn(name = "posts_idx", nullable = false)  // posts 테이블의 idx를 참조
    private PostsEntity post; // posts 테이블과의 관계

    @ManyToOne
    @JoinColumn(name = "users_idx", nullable = false)  // users 테이블의 idx를 참조
    private MainEntity user; // users 테이블과의 관계

    @Column(name = "nickname", nullable = false) // nullable을 false로 명시
    private String nickname; // 작성자 닉네임

    @Column(nullable = false, columnDefinition = "CLOB")
    private String content; // 댓글 내용

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate; // 댓글 작성 시간
}
