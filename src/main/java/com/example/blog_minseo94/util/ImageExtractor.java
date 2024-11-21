package com.example.blog_minseo94.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ImageExtractor {

    private static final String DEFAULT_IMAGE_URL = "/images/default_image.png"; // 경로 수정

    // HTML content에서 이미지 URL을 추출하는 메서드
    public static List<String> extractImageUrls(String content) {
        List<String> imageUrls = new ArrayList<>();
        // Jsoup을 사용하여 HTML 파싱
        Document doc = Jsoup.parse(content);
        // 이미지 태그들을 선택
        Elements images = doc.select("img");

        // 각 이미지 태그의 src 속성에서 이미지 URL 추출
        for (Element img : images) {
            imageUrls.add(img.attr("src"));
        }

        // 이미지가 없을 경우 기본 이미지 추가
        if (imageUrls.isEmpty()) {
            imageUrls.add(DEFAULT_IMAGE_URL);
        }

        return imageUrls;
    }
}
