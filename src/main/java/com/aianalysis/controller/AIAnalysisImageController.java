package com.aianalysis.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Controller
@RequestMapping("ai-analysis-image")
public class AIAnalysisImageController {

  @Autowired
  JdbcTemplate jdbcTemplate;

  private final RestTemplate restTemplate = new RestTemplate();

  @GetMapping("")
  public String show() {
    return "index";
  }

  @PostMapping("")
  public String create() {
    // 特定の画像ファイルへのパス
    String imagePath = "/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg";

    // APIリクエストURL
    String url = "http://127.0.0.1:3000/fail";

    Map<String, String> requestBody = new HashMap<String, String>();
    requestBody.put("image_path", imagePath);

    // APIリクエスト
    try {
      AiAnalysisImageResult response = restTemplate.postForObject(url, requestBody, AiAnalysisImageResult.class);

      if (!response.isSuccess()) {
        // リクエストが失敗
        System.out.println("リクエスト失敗");
        return "index";
      }

      // データ登録

    } catch (Exception e) {
      System.out.println(e);
    }

    return "index";
  }

}

@Data
class AiAnalysisImageResult {
  private boolean success;
  private String messsage;

  @JsonProperty("estimated_data")
  private Map<String, Integer> estimatedDataMap;

}
