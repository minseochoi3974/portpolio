package com.example.blog_minseo94.repository;

import com.example.blog_minseo94.entity.VisitorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VisitorRepository extends JpaRepository<VisitorEntity, Long> {

    // 특정 날짜에 방문 기록 조회 (년도-월-일만 비교)
    List<VisitorEntity> findByVisitedDate(LocalDate visitedDate);

    // 특정 사용자의 전체 방문 기록 조회 (viewer_id를 기준으로)
    List<VisitorEntity> findByViewerIdx(Long viewerId);

    // 특정 사용자의 오늘 방문 기록 조회 (viewer_id로 중복 방지용)
    @Query("SELECT v FROM VisitorEntity v WHERE v.viewer.idx = :viewerId AND v.visitedDate = :today")
    List<VisitorEntity> findTodayVisitsByViewerId(@Param("viewerId") Long viewerId, @Param("today") LocalDate today);

    // 특정 사용자의 최근 10개 방문 기록 조회 (viewer_id를 기준으로 내림차순)
    @Query("SELECT v FROM VisitorEntity v WHERE v.viewer.idx = :viewerId ORDER BY v.visitedDate DESC")
    List<VisitorEntity> findTop10ByViewerIdOrderByVisitedDateDesc(@Param("viewerId") Long viewerId);

    @Query("SELECT v FROM VisitorEntity v WHERE v.visitedUser.idx = :visitedUserId ORDER BY v.visitedDate DESC")
    List<VisitorEntity> findVisitorsByVisitedUserId(@Param("visitedUserId") Long visitedUserId);

}
