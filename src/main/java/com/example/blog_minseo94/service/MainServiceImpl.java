package com.example.blog_minseo94.service;

import com.example.blog_minseo94.entity.MainEntity;
import com.example.blog_minseo94.repository.MainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor

public class MainServiceImpl implements MainService {

    private final MainRepository mainRepository;

    // 사용자를 저장하는 메소드
    @Override
    public void saveUser(MainEntity user) {
        // 나이 계산
        if (user.getBirthday() != null) {
            int age = Period.between(user.getBirthday(), LocalDate.now()).getYears();
            user.setAge(age);
        }

        // 현재 날짜를 today 필드에 설정
        user.setToday(LocalDate.now());  // 오늘 날짜 설정

        // 사용자 정보를 데이터베이스에 저장
        mainRepository.save(user);
    }

    // ID로 사용자를 찾는 메소드
    @Override
    public MainEntity findUserById(String id) {
        return mainRepository.findById(id).orElse(null); // 사용자가 없으면 null 반환
    }

    // 비밀번호를 확인하는 메소드
    @Override
    public boolean checkPassword(String userId, String pwd) {
        // 사용자 ID로 사용자를 찾은 후 비밀번호가 일치하는지 확인
        MainEntity user = findUserById(userId);
        // 사용자 정보가 존재하고, 비밀번호가 일치하는지 확인
        if (user != null) {
            return user.getPwd().equals(pwd);
        }
        return false; // 사용자 정보가 없거나 비밀번호가 일치하지 않는 경우 false 반환
    }

    @Override
    public void updateUser(String userId, MainEntity modifiedUser) {
        // 사용자 ID로 사용자 정보 조회, 없으면 예외 처리
        MainEntity user = mainRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 정보 업데이트
        if (modifiedUser.getPwd() != null && !modifiedUser.getPwd().isEmpty()) {
            user.setPwd(modifiedUser.getPwd());
        }
        user.setNickname(modifiedUser.getNickname());
        user.setEmail(modifiedUser.getEmail());
        user.setBirthday(modifiedUser.getBirthday());
        user.setAddress(modifiedUser.getAddress());

        // 생년월일에 따른 나이 계산
        if (modifiedUser.getBirthday() != null) {
            int age = Period.between(modifiedUser.getBirthday(), LocalDate.now()).getYears();
            user.setAge(age);
        }

        // 수정된 사용자 정보 저장
        mainRepository.save(user);
    }
    // ID 중복 확인 메소드
    @Override
    public boolean checkIdExists(String id) {
        return mainRepository.existsById(id);
    }

    // 닉네임 중복 확인 메소드
    @Override
    public boolean checkNicknameExists(String nickname) {
        return mainRepository.existsByNickname(nickname);
    }

    // 이메일 중복 확인 메소드
    @Override
    public boolean checkEmailExists(String email) {
        return mainRepository.existsByEmail(email);
    }

    //닉네임으로 사용자 찾기
    @Override
    public MainEntity findByNickname(String nickname) {
        return mainRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임을 가진 사용자를 찾을 수 없습니다: " + nickname));
    }

}
