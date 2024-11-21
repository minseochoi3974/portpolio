package com.example.blog_minseo94.repository;

import com.example.blog_minseo94.entity.BlogSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogSettingsRepository extends JpaRepository<BlogSettingsEntity, Long> {
    // 사용자 idx로 블로그 설정을 가져오는 메서드
    Optional<BlogSettingsEntity> findByUserIdx(Long userIdx);
}
