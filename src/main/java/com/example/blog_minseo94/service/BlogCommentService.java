package com.example.blog_minseo94.service;

import com.example.blog_minseo94.dto.BlogCommentDTO;
import com.example.blog_minseo94.entity.BlogCommentEntity;
import com.example.blog_minseo94.entity.PostsEntity;

import java.util.List;

public interface BlogCommentService {
    void addComment(BlogCommentDTO commentDTO);  // 댓글 작성

    List<BlogCommentDTO> getCommentsByPostId(Long postId);  // 댓글 조회

    BlogCommentDTO getCommentById(Long commentIdx);  // 댓글 ID로 댓글 조회

    // 댓글 삭제
    void deleteComment(Long commentIdx, Long userId); // 삭제 요청한 사용자가 작성자인지 확인

    // 댓글 수정
    void updateComment(Long commentId, String content, Long userId);

    //댓글의 ID를 가지고 그 댓글이 속한 게시글의 ID를 찾아오는 기능
    Long findPostIdByCommentId(Long commentId);

    // 댓글 수 조회 메서드
    int countCommentsByPostId(Long postId);

}
