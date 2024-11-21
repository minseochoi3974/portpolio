package com.example.blog_minseo94.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "blog_settings")
@Data
@ToString
public class BlogSettingsEntity {

    @Id
    private Long userIdx; // users 테이블과 연결된 사용자 ID

    @Column(name = "blog_image")
    private String blogImage;

    @Column(name = "blog_nickname")
    private String blogNickname;

    @Column(name = "blog_message")
    private String blogMessage;

    @Column(name = "blog_title")
    private String blogTitle;

    @Column(name = "layout")
    private Integer layout; // 블로그 레이아웃 정보

    @OneToOne
    @JoinColumn(name = "user_idx", referencedColumnName = "idx", insertable = false, updatable = false)
    private MainEntity user; // MainEntity와 연결된 외래 키

}
