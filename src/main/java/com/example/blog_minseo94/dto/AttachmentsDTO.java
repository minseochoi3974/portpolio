package com.example.blog_minseo94.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AttachmentsDTO {
    private Long attachmentId; // 첨부 파일 고유 ID
    private Long postIdx; // posts 테이블과 연결된 게시물 ID
    private String attachmentUrl; // 첨부 파일 또는 이미지 URL/경로
    private String attachmentType; // 파일 유형 (file 또는 image)
    private LocalDate uploadedDate; // 파일 업로드 날짜
    private String filename; // 파일명
}
