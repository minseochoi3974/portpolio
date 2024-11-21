package com.example.blog_minseo94.service;

import com.example.blog_minseo94.entity.MainEntity;

public interface MainService {

    // 회원 저장 메소드 시그니처 (저장할 메소드가 있다는 선언만 함)
    void saveUser(MainEntity user);

    // ID로 사용자 찾기 메소드 시그니처 (찾기 기능이 있다는 선언만 함)
    MainEntity findUserById(String id);

    // 비밀번호 확인 메소드 추가
    boolean checkPassword(String userId, String pwd);

    // 사용자 정보 수정 메소드
    void updateUser(String userId, MainEntity modifiedUser);

    // 아이디 중복 확인 메소드 추가
    boolean checkIdExists(String id);

    // 닉네임 중복 확인 메소드 추가
    boolean checkNicknameExists(String nickname);

    // 이메일 중복 확인 메소드 추가
    boolean checkEmailExists(String email);

    // 닉네임으로 사용자 찾기
    MainEntity findByNickname(String nickname);
}