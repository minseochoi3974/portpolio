package com.example.blog_minseo94.repository;

import com.example.blog_minseo94.entity.MainEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MainRepository extends JpaRepository<MainEntity, Long> {
    boolean existsById(String id);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<MainEntity> findById(String id);
    // 닉네임으로 사용자 찾기
    Optional<MainEntity> findByNickname(String nickname);
}
