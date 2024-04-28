package com.aianalysis.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.aianalysis.model.AIAnalysisImageResult;
import com.aianalysis.model.AIAnalysisLog;
import com.aianalysis.repository.AIAnalysisLogRepository;

import jakarta.transaction.Transactional;

@Service
public class AIAnalysisImageService {

  private final AIAnalysisLogRepository aiAnalysisLogRepository;

  private final RestTemplate restTemplate = new RestTemplate();

  // APIリクエストURL
  private final String url = "http://127.0.0.1:3000/success";

  public AIAnalysisImageService(AIAnalysisLogRepository aiAnalysisLogRepository) {
    this.aiAnalysisLogRepository = aiAnalysisLogRepository;
  };

  /**
   * 画像所属Classを取得
   * 
   * @param imagePath 画像のファイルパス
   * @return 画像情報取得結果
   */
  public AIAnalysisImageResult getAIAnalysisImageInfo(String imagePath) {

    Map<String, String> requestBody = new HashMap<String, String>();
    requestBody.put("image_path", imagePath);

    // リクエストのタイムスタンプ
    LocalDateTime requestTimestamp = LocalDateTime.now();

    AIAnalysisImageResult aiAnalysisImageResult = restTemplate.postForObject(url, requestBody,
        AIAnalysisImageResult.class);

    // レスポンスタイムスタンプ
    LocalDateTime responseTimestamp = LocalDateTime.now();

    aiAnalysisImageResult.setImagePath(imagePath);
    aiAnalysisImageResult.setRequestTimestamp(requestTimestamp);
    aiAnalysisImageResult.setResponseTimestamp(responseTimestamp);

    if (!aiAnalysisImageResult.isSuccess()) {
      // リクエストが失敗
      System.out.println("リクエストが失敗しました。");
    }

    return aiAnalysisImageResult;
  }

  /**
   * 画像情報の保存
   * 
   * @param aiAnalysisImageResult 画像情報取得結果
   */
  public void saveAIAnalysisLog(AIAnalysisImageResult aiAnalysisImageResult) {
    AIAnalysisLog aiAnalysisLog = new AIAnalysisLog();
    aiAnalysisLog.setImagePath(aiAnalysisImageResult.getImagePath());
    aiAnalysisLog.setSuccess(booleanToInteger(aiAnalysisImageResult.isSuccess()));
    aiAnalysisLog.setMessage(aiAnalysisImageResult.getMessage());
    aiAnalysisLog.setClassColumn(aiAnalysisImageResult.getEstimatedData().getClassColumn());
    aiAnalysisLog.setConfidence(aiAnalysisImageResult.getEstimatedData().getConfidence());
    aiAnalysisLog.setRequestTimestamp(aiAnalysisImageResult.getRequestTimestamp());
    aiAnalysisLog.setResponseTimestamp(aiAnalysisImageResult.getResponseTimestamp());

    createAIAnalysisLog(aiAnalysisLog);
  }

  /**
   * 画像情報取得結果を登録
   * 
   * @param aiAnalysisLog 登録データ
   */
  @Transactional
  private void createAIAnalysisLog(AIAnalysisLog aiAnalysisLog) {
    aiAnalysisLogRepository.save(aiAnalysisLog);
  }

  /**
   * boolan型をInteger型に変換
   * 
   * @param value boolean型の値
   * @return Integer型の変換値
   */
  private Integer booleanToInteger(Boolean value) {
    return value ? 1 : 0;
  }

}
