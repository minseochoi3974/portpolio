package com.example.blog_minseo94.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
public class MainDTO {
    private long idx;
    private String id;
    private String pwd;
    private String name;
    private LocalDate birthday;
    private int age;
    private String address;
    private String detailAddress;
    private String postalCode;
    private String nickname;
    private String email;
    private LocalDate today;

}
