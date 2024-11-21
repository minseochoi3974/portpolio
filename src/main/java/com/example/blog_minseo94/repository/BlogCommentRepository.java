package com.example.blog_minseo94.repository;

import com.example.blog_minseo94.entity.BlogCommentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


@Repository
public interface BlogCommentRepository extends JpaRepository<BlogCommentEntity, Long> {

    // 특정 게시글의 모든 댓글을 조회
    List<BlogCommentEntity> findByPostIdx(Long postId);

    // 특정 사용자가 작성한 댓글 조회 (옵션)
    List<BlogCommentEntity> findByUserIdx(Long userId);

    // 특정 게시글의 댓글 수 조회
    @Query("SELECT COUNT(c) FROM BlogCommentEntity c WHERE c.post.idx = :postId")
    int countCommentsByPostId(Long postId);

    // 특정 게시물의 댓글 수를 세는 메서드
    int countByPostIdx(Long postId);

}
