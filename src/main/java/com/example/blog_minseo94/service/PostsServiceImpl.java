package com.example.blog_minseo94.service;

import com.example.blog_minseo94.entity.AttachmentsEntity;
import com.example.blog_minseo94.entity.BlogSettingsEntity;
import com.example.blog_minseo94.entity.PostsEntity;
import com.example.blog_minseo94.repository.AttachmentsRepository;
import com.example.blog_minseo94.repository.BlogSettingsRepository;
import com.example.blog_minseo94.repository.PostsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostsServiceImpl implements PostsService {

    private final PostsRepository postsRepository;
    private final AttachmentsRepository attachmentsRepository;
    private final BlogSettingsRepository blogSettingsRepository; // 블로그 설정 레포지토리 추가

    @Override
    public void savePost(PostsEntity post) {
        postsRepository.save(post); // 게시글 저장
    }

    // ID로 게시물 찾기 메소드
    @Override
    public PostsEntity findPostById(Long id) {
        return postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다.")); // 게시물 ID로 찾기
    }

    //게시글 삭제
    @Override
    public void deletePost(Long id) {
        postsRepository.deleteById(id);
    }

    //게시글 작성 시 파일 처리
    @Override
    public void saveAttachment(PostsEntity post, String attachmentUrl, String attachmentType, String filename) {
        AttachmentsEntity attachment = new AttachmentsEntity();
        attachment.setPost(post); // 게시글과 연관
        attachment.setAttachmentUrl(attachmentUrl);
        attachment.setAttachmentType(attachmentType);
        attachment.setUploadedDate(java.time.LocalDate.now());
        attachment.setFilename(filename); // 파일 이름 저장 (이 부분 추가됨)


        attachmentsRepository.save(attachment); // 첨부 파일 저장
    }

    // **새로 추가된 부분: 첨부 파일 리스트 가져오는 메서드 구현**
    @Override
    public List<AttachmentsEntity> getAttachmentsByPostId(Long postId) {
        return attachmentsRepository.findByPostIdx(postId);
    }

    @Override
    public BlogSettingsEntity findBlogSettingsByUserIdx(Long userIdx) {
        return blogSettingsRepository.findByUserIdx(userIdx)
                .orElse(null);  // 블로그 설정이 없을 경우 null 반환
    }

    @Override
    public void updateBlogSettings(BlogSettingsEntity blogSettings) {
        blogSettingsRepository.save(blogSettings); // 블로그 설정 업데이트
    }

    // 최신게시물 3개
    @Override
    public List<PostsEntity> getTop3Posts(Long userId) {
        return postsRepository.findTop3PostsByUserId(userId, PageRequest.of(0, 3))
                .stream()
                .filter(post -> post.getVisibility().equals("PUBLIC") || post.getUser().getIdx().equals(userId))
                // 전체 공개이거나 작성자가 본인이면 필터링
                .collect(Collectors.toList());
    }

    // 인기게시물 3개
    @Override
    public List<PostsEntity> getTop3PopularPosts(Long userId) {
        return postsRepository.findTop3ByUserIdOrderByViewsDesc(userId, PageRequest.of(0, 3))
                .stream()
                .filter(post -> post.getVisibility().equals("PUBLIC") || post.getUser().getIdx().equals(userId))
                // 전체 공개이거나 작성자가 본인이면 필터링
                .collect(Collectors.toList());
    }

    // 제목에 keyword가 포함된 게시물 검색
    @Override
    public List<PostsEntity> searchByTitle(String title) {
        return postsRepository.findByTitleContaining(title);
    }

    // 작성자에 keyword가 포함된 게시물 검색
    @Override
    public List<PostsEntity> searchByNickname(String nickname) {
        return postsRepository.findByNicknameContaining(nickname);
    }

    // myblog 게시글 리스트 페이징
    @Override
    public Page<PostsEntity> findPostsByUserId(Long userIdx, Pageable pageable) {
        return postsRepository.findByUserIdx(userIdx, pageable);
    }


    //게시물의 좋아요 수를 증가
    @Override
    @Transactional
    public void incrementLikes(Long postId) {
        postsRepository.incrementLikes(postId);
    }

    // 게시물의 좋아요 수를 감소
    @Override
    @Transactional
    public void decrementLikes(Long postId) {
        postsRepository.decrementLikes(postId);
    }

    @Override
    public List<PostsEntity> getTrendingPosts() {
        // 전체 공개 게시물 중 최신순으로 가져오기
        return postsRepository.findByVisibilityOrderByCreatedDateDesc("public");
    }



}
