package com.example.blog_minseo94.repository;

import com.example.blog_minseo94.entity.PostsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<PostsEntity, Long> {

    List<PostsEntity> findByTitleContaining(String title);

    List<PostsEntity> findByNicknameContaining(String nickname);

    // // 내림차순으로 페이징 처리된 게시물 가져오기 (myblog에 list)
    Page<PostsEntity> findAllByOrderByCreatedDateDesc(Pageable pageable);

    // 작성자 ID로 게시글 조회
    Page<PostsEntity> findByUserId(Long userId, Pageable pageable);

    // 사용자의 게시물 조회
    Page<PostsEntity> findByUserIdx(Long userIdx, Pageable pageable);

    // 좋아요 수 증가
    @Modifying
    @Query("UPDATE PostsEntity p SET p.likes = p.likes + 1 WHERE p.idx = :postId")
    void incrementLikes(@Param("postId") Long postId);

    // 좋아요 수 감소
    @Modifying
    @Query("UPDATE PostsEntity p SET p.likes = p.likes - 1 WHERE p.idx = :postId")
    void decrementLikes(@Param("postId") Long postId);

    /**
     * 게시글 최신게시물 3개
     * 주어진 사용자 ID에 해당하는 사용자가 작성한 게시물들을
     * 생성일 기준으로 내림차순 정렬하여 상위 3개의 게시물을 반환합니다.
     *
     * @param userId 게시물을 작성한 사용자의 ID
     * @return 해당 사용자가 작성한 최신 게시물 3개 리스트
     */
    // 최신 게시물 3개 가져오기 (visibility = 'PUBLIC'이거나 본인의 게시물)
    @Query("SELECT p FROM PostsEntity p WHERE (p.user.idx = :userId OR p.visibility = 'PUBLIC') ORDER BY p.createdDate DESC")
    List<PostsEntity> findTop3PostsByUserId(@Param("userId") Long userId, Pageable pageable);

    // 인기 게시물 3개 가져오기 (visibility = 'PUBLIC'이거나 본인의 게시물)
    @Query("SELECT p FROM PostsEntity p WHERE (p.user.idx = :userId OR p.visibility = 'PUBLIC') ORDER BY p.views DESC")
    List<PostsEntity> findTop3ByUserIdOrderByViewsDesc(@Param("userId") Long userId, Pageable pageable);

    Optional<PostsEntity> findById(Long postId);

    // 전체 공개 게시물 중 최신 게시물을 가져오는 메소드 (createdDate 기준 내림차순 정렬)
    List<PostsEntity> findByVisibilityOrderByCreatedDateDesc(String visibility);


}
