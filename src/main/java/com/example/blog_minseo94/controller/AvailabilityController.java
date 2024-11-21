package com.example.blog_minseo94.controller;

import com.example.blog_minseo94.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AvailabilityController {

    private final MainService mainService; // MainService로 변경하여 비즈니스 로직을 위임

    @Autowired
    public AvailabilityController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/check-availability")
    public Map<String, Boolean> checkAvailability(@RequestParam("field") String field,
                                                  @RequestParam("value") String value) {
        boolean exists = false;

        // 필드에 따라 중복 여부 확인을 서비스에 위임
        switch (field) {
            case "id":
                exists = mainService.checkIdExists(value);
                break;
            case "nickname":
                exists = mainService.checkNicknameExists(value);
                break;
            case "email":
                exists = mainService.checkEmailExists(value);
                break;
            default:
                throw new IllegalArgumentException("잘못된 필드: " + field);
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response; // JSON 응답으로 중복 여부 반환
    }
}
