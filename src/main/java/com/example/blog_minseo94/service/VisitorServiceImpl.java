package com.example.blog_minseo94.service;

import com.example.blog_minseo94.dto.VisitorDTO;
import com.example.blog_minseo94.entity.MainEntity;
import com.example.blog_minseo94.entity.VisitorEntity;
import com.example.blog_minseo94.repository.MainRepository;
import com.example.blog_minseo94.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final MainRepository mainRepository;

    // 방문 기록 추가 (중복 방지)
    @Transactional
    @Override
    public void addVisitor(Long viewerId, Long visitedUserId) {
        LocalDate today = LocalDate.now();

        // 오늘 이미 방문했는지 확인
        List<VisitorEntity> todayVisits = visitorRepository.findTodayVisitsByViewerId(viewerId, today);
        if (todayVisits.isEmpty()) {
            MainEntity viewer = mainRepository.findById(viewerId)
                    .orElseThrow(() -> new RuntimeException("방문자 정보를 찾을 수 없음"));
            MainEntity visitedUser = mainRepository.findById(visitedUserId)
                    .orElseThrow(() -> new RuntimeException("게시글 작성자 정보를 찾을 수 없음"));

            VisitorEntity visitor = new VisitorEntity();
            visitor.setViewer(viewer);
            visitor.setVisitedUser(visitedUser);
            visitor.setVisitedDate(today);
            visitorRepository.save(visitor);
        }
    }

    // 특정 사용자의 오늘 방문자 수
    @Override
    public int getTodayVisitorCount(Long viewerId) {
        return visitorRepository.findTodayVisitsByViewerId(viewerId, LocalDate.now()).size();
    }

    // 특정 사용자의 전체 누적 방문자 수
    @Override
    public int getTotalVisitorCount(Long viewerId) {
        int count = visitorRepository.findByViewerIdx(viewerId).size();
        System.out.println("Total Visitor Count for user " + viewerId + ": " + count);
        return count;
    }

    // 특정 날짜의 특정 사용자의 방문 기록 가져오기
    @Override
    public List<VisitorEntity> getVisitorsByDate(Long viewerId, LocalDate date) {
        return visitorRepository.findTodayVisitsByViewerId(viewerId, date);
    }

    // 최근 방문자 리스트 가져오기 (예: 최근 10명)
    @Override
    public List<VisitorDTO> getRecentVisitors(Long visitedUserId) {
        List<VisitorEntity> recentVisitors = visitorRepository.findVisitorsByVisitedUserId(visitedUserId);
        return recentVisitors.stream()
                .map(visitor -> {
                    VisitorDTO dto = new VisitorDTO();
                    dto.setId(visitor.getId());
                    dto.setViewerId(visitor.getViewer().getIdx());
                    dto.setVisitedUserId(visitor.getVisitedUser().getIdx());
                    dto.setNickname(visitor.getViewer().getNickname()); // viewer의 닉네임 설정
                    dto.setVisitedDate(visitor.getVisitedDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}