package com.example.blog_minseo94.service;

import com.example.blog_minseo94.entity.AttachmentsEntity;
import com.example.blog_minseo94.entity.BlogSettingsEntity;
import com.example.blog_minseo94.entity.PostsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PostsService {
    // 게시글 저장 메소드
    void savePost(PostsEntity post);

    // ID로 게시물 찾기
    PostsEntity findPostById(Long id);

    // 첨부 파일 저장 메소드
    void saveAttachment(PostsEntity post, String attachmentUrl, String attachmentType, String filename);

    // **새로 추가된 부분: 게시글에 해당하는 첨부 파일 리스트 가져오는 메소드**
    List<AttachmentsEntity> getAttachmentsByPostId(Long postId);

    // 사용자 idx로 블로그 설정 가져오는 메소드
    BlogSettingsEntity findBlogSettingsByUserIdx(Long userIdx);

    // 블로그 설정 업데이트 메소드
    void updateBlogSettings(BlogSettingsEntity blogSettings);

    //최신게시물 3개 가져오는 메소드
    List<PostsEntity> getTop3Posts(Long userId);

    // 인기 게시물 3개 가져오는 메소드
    List<PostsEntity> getTop3PopularPosts(Long userId);

    //검색할때 title 필터
    List<PostsEntity> searchByTitle(String title);

    //검색할때 nickname 필터
    List<PostsEntity> searchByNickname(String nickname);

    // myblog 게시글 리스트 페이징
    Page<PostsEntity> findPostsByUserId(Long userIdx, Pageable pageable);


    // 좋아요 수 증가
    @Modifying
    @Query("UPDATE PostsEntity p SET p.likes = p.likes + 1 WHERE p.idx = :postId")
    void incrementLikes(@Param("postId") Long postId);

    // 좋아요 수 감소
    @Modifying
    @Query("UPDATE PostsEntity p SET p.likes = p.likes - 1 WHERE p.idx = :postId")
    void decrementLikes(@Param("postId") Long postId);

    // 게시글 삭제 메소드
    void deletePost(Long id);

    // 전체 공개 게시물 목록 가져오기
    List<PostsEntity> getTrendingPosts();

}
