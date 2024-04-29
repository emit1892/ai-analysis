package com.aianalysis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.aianalysis.model.AIAnalysisImageResult;
import com.aianalysis.model.AIAnalysisLog;
import com.aianalysis.model.EstimatedData;
import com.aianalysis.repository.AIAnalysisLogRepository;

@SpringBootTest
public class AIAnalysisImageServiceTest {
  @Mock
  private AIAnalysisLogRepository aiAnalysisLogRepository;

  @InjectMocks
  private AIAnalysisImageService aiAnalysisImageService;

  @BeforeEach
  private void setup() {
    aiAnalysisImageService = new AIAnalysisImageService(aiAnalysisLogRepository);
    ReflectionTestUtils.setField(aiAnalysisImageService, "url", "http://127.0.0.1:3000", String.class);
  }

  @Test
  public void 画像所属Classを取得する() throws Exception {
    AIAnalysisImageResult aiAnalysisImageResult = aiAnalysisImageService
        .getAIAnalysisImageInfo("/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg");
    assertEquals(true, aiAnalysisImageResult.isSuccess());
    assertEquals("success", aiAnalysisImageResult.getMessage());
    assertEquals(3, aiAnalysisImageResult.getEstimatedData().getClassColumn());
    assertEquals(BigDecimal.valueOf(0.8683), aiAnalysisImageResult.getEstimatedData().getConfidence());
  }

  @Test
  public void リクエストが失敗した場合の画像所属Classを取得する() throws Exception {
    ReflectionTestUtils.setField(aiAnalysisImageService, "url", "http://127.0.0.1:3000", String.class);

    AIAnalysisImageResult aiAnalysisImageResult = aiAnalysisImageService
        .getAIAnalysisImageInfo("/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/error.jpg");
    assertEquals(false, aiAnalysisImageResult.isSuccess());
    assertEquals("Error:E50012", aiAnalysisImageResult.getMessage());
    assertEquals(null, aiAnalysisImageResult.getEstimatedData().getClassColumn());
    assertEquals(null, aiAnalysisImageResult.getEstimatedData().getConfidence());
  }

  @Test
  public void 画像情報の保存をする() throws Exception {
    LocalDateTime requestTimestamp = LocalDateTime.now();
    LocalDateTime responseTimestamp = LocalDateTime.now();

    AIAnalysisImageResult aiAnalysisImageResult = new AIAnalysisImageResult();
    aiAnalysisImageResult.setImagePath("/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg");
    aiAnalysisImageResult.setSuccess(true);
    aiAnalysisImageResult.setMessage("success");
    EstimatedData estimatedData = new EstimatedData();
    estimatedData.setClassColumn(3);
    estimatedData.setConfidence(BigDecimal.valueOf(0.8683));
    aiAnalysisImageResult.setEstimatedData(estimatedData);
    aiAnalysisImageResult.setRequestTimestamp(requestTimestamp);
    aiAnalysisImageResult.setResponseTimestamp(responseTimestamp);

    aiAnalysisImageService.saveAIAnalysisLog(aiAnalysisImageResult);

    AIAnalysisLog aiAnalysisLog = new AIAnalysisLog();
    aiAnalysisLog.setImagePath("/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg");
    aiAnalysisLog.setSuccess(1);
    aiAnalysisLog.setMessage("success");
    aiAnalysisLog.setClassColumn(3);
    aiAnalysisLog.setConfidence(BigDecimal.valueOf(0.8683));
    aiAnalysisLog.setRequestTimestamp(requestTimestamp);
    aiAnalysisLog.setResponseTimestamp(responseTimestamp);

    verify(aiAnalysisLogRepository, times(1)).save(aiAnalysisLog);
  }
}
