package com.example.blog_minseo94.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@ToString
public class AttachmentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long attachmentId;

    @ManyToOne
    @JoinColumn(name = "post_idx", referencedColumnName = "idx", nullable = false, foreignKey = @ForeignKey(name = "fk_post"))
    private PostsEntity post; // 게시글과 연결된 외래키

    @Column(name = "attachment_url", nullable = false)
    private String attachmentUrl; // 첨부 파일 URL/경로

    @Column(name = "attachment_type", nullable = false)
    private String attachmentType; // 첨부 파일 유형 (file, image)

    @Column(name = "uploaded_date", nullable = false)
    private LocalDate uploadedDate = LocalDate.now(); // 파일 업로드 날짜

    @Column(name = "filename", nullable = false)
    private String filename; // 파일명

    @Override
    public String toString() {
        return "AttachmentsEntity{" +
                "attachmentId=" + attachmentId +
                ", attachmentUrl='" + attachmentUrl + '\'' +
                ", attachmentType='" + attachmentType + '\'' +
                ", uploadedDate=" + uploadedDate +
                ", filename='" + filename + '\'' +
                '}';
    }
}
