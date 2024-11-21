package com.example.blog_minseo94.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
public class PostsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long idx;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private int views = 0;

    @ManyToOne
    @JoinColumn(name = "users_idx", nullable = false, foreignKey = @ForeignKey(name = "fk_user"))
    private MainEntity user; // 작성자와 연결된 외래키

    @Column(nullable = false)
    private String visibility;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachmentsEntity> attachments; // 첨부 파일 리스트

    @Column(nullable = false)
    private Long likes = 0L; // 좋아요 수 추가 (기본값 0)

    @Override
    public String toString() {
        return "PostsEntity{" +
                "idx=" + idx +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", nickname='" + nickname + '\'' +
                ", createdDate=" + createdDate +
                ", views=" + views +
                ", visibility='" + visibility + '\'' +
                '}';
    }


}
