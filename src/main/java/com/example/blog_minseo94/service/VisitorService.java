package com.example.blog_minseo94.service;

import com.example.blog_minseo94.dto.VisitorDTO;
import com.example.blog_minseo94.entity.VisitorEntity;
import java.time.LocalDate;
import java.util.List;

public interface VisitorService {

    // 방문 기록 추가
    void addVisitor(Long viewerId, Long visitedUserId);

    // 특정 사용자의 오늘 방문자 수 가져오기 (viewerId 기준)
    int getTodayVisitorCount(Long viewerId);

    // 특정 사용자의 전체 누적 방문자 수 가져오기 (viewerId 기준)
    int getTotalVisitorCount(Long viewerId);

    // 특정 날짜의 특정 사용자의 방문 기록 가져오기 (viewerId 기준)
    List<VisitorEntity> getVisitorsByDate(Long viewerId, LocalDate date);

    // 특정 사용자의 최근 방문자 리스트 가져오기 (viewerId 기준)
    List<VisitorDTO> getRecentVisitors(Long viewerId);

}
