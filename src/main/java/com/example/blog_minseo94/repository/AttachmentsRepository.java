package com.example.blog_minseo94.repository;

import com.example.blog_minseo94.entity.AttachmentsEntity;
import com.example.blog_minseo94.entity.PostsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentsRepository extends JpaRepository<AttachmentsEntity, Long> {
    List<AttachmentsEntity> findByPost(PostsEntity post); // 게시물로 첨부 파일 검색
    List<AttachmentsEntity> findByPostIdx(Long postId); // 게시글 ID로 첨부 파일 리스트 가져오기


}
