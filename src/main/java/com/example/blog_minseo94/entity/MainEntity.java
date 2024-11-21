package com.example.blog_minseo94.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;

@Entity
@ToString
@Table(name = "users")
@NoArgsConstructor
@Data
public class MainEntity {
    @Id //PRIMARY KEY 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long idx;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "id", nullable = false, unique = true)  // unique 제약조건 추가
    private String id;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column
    private LocalDate today;

    @Override
    public String toString() {
        return "MainEntity{" +
                "idx=" + idx +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
