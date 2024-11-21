package com.example.blog_minseo94.service;

import com.example.blog_minseo94.dto.BlogCommentDTO;
import com.example.blog_minseo94.entity.BlogCommentEntity;
import com.example.blog_minseo94.entity.MainEntity;
import com.example.blog_minseo94.entity.PostsEntity;
import com.example.blog_minseo94.repository.BlogCommentRepository;
import com.example.blog_minseo94.repository.MainRepository;
import com.example.blog_minseo94.repository.PostsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class BlogCommentServiceImpl implements BlogCommentService {

    private final BlogCommentRepository commentRepository;
    private final PostsRepository postsRepository;
    private final MainRepository mainRepository;

    //댓글저장
    @Override
    @Transactional
    public void addComment(BlogCommentDTO commentDTO) {
        // 게시물과 유저 정보를 가져옴
        PostsEntity post = postsRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        MainEntity user = mainRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 댓글 엔티티 생성 및 저장
        BlogCommentEntity commentEntity = new BlogCommentEntity();
        commentEntity.setPost(post);
        commentEntity.setUser(user);
        commentEntity.setNickname(commentDTO.getNickname());
        commentEntity.setContent(commentDTO.getContent());
        commentEntity.setCreatedDate(commentDTO.getCreatedDate());

        // 댓글 저장
        BlogCommentEntity savedComment = commentRepository.save(commentEntity);

        // 저장된 commentIdx를 DTO에 설정
        commentDTO.setCommentIdx(savedComment.getCommentIdx());
    }


    @Override
    public List<BlogCommentDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findAll().stream()
                .filter(comment -> comment.getPost().getIdx().equals(postId))  // getIdx() 사용
                .map(comment -> {
                    BlogCommentDTO commentDTO = new BlogCommentDTO();
                    commentDTO.setCommentIdx(comment.getCommentIdx());
                    commentDTO.setPostId(comment.getPost().getIdx());  // getIdx() 사용
                    commentDTO.setUserId(comment.getUser().getIdx());  // MainEntity의 getIdx() 사용
                    commentDTO.setNickname(comment.getNickname());
                    commentDTO.setContent(comment.getContent());
                    commentDTO.setCreatedDate(comment.getCreatedDate());
                    return commentDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public BlogCommentDTO getCommentById(Long commentIdx) {
        BlogCommentEntity comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        BlogCommentDTO commentDTO = new BlogCommentDTO();
        commentDTO.setCommentIdx(comment.getCommentIdx());
        commentDTO.setPostId(comment.getPost().getIdx());
        commentDTO.setUserId(comment.getUser().getIdx());
        commentDTO.setNickname(comment.getNickname());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedDate(comment.getCreatedDate());

        return commentDTO;
    }

    //댓글 수정 삭제
    @Override
    @Transactional
    public void deleteComment(Long commentIdx, Long userId) {
        BlogCommentEntity comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        // 댓글 작성자인지 확인
        if (!comment.getUser().getIdx().equals(userId)) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }

        commentRepository.deleteById(commentIdx);
    }

    // 댓글 수정을 위한 댓글 확인
    @Override
    public Long findPostIdByCommentId(Long commentId) {
        // 댓글 엔티티에서 게시글을 찾아서 그 게시글의 ID를 반환
        BlogCommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));
        return comment.getPost().getIdx();  // 댓글이 속한 게시글의 ID 반환
    }

    //댓글 수정 후 업데이트 메서드
    @Override
    @Transactional
    public void updateComment(Long commentId, String content, Long userId) {
        BlogCommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        // 댓글 작성자인지 확인
        if (!comment.getUser().getIdx().equals(userId)) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }

        comment.setContent(content);  // 댓글 내용 업데이트
        commentRepository.save(comment);  // 수정된 댓글 저장
    }


    // 댓글 수를 가져오는 메서드
    @Override
    public int countCommentsByPostId(Long postId) {
        return commentRepository.countCommentsByPostId(postId);
    }
}
