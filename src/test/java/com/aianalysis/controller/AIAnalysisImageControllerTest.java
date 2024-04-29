package com.aianalysis.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.aianalysis.form.ImagePathForm;
import com.aianalysis.model.AIAnalysisImageResult;
import com.aianalysis.model.EstimatedData;
import com.aianalysis.service.AIAnalysisImageService;

@SpringBootTest
@TestPropertySource(properties = "api.request.url=http://example.com/")
public class AIAnalysisImageControllerTest {

  @Mock
  private AIAnalysisImageService aiAnalysisImageService;

  private AIAnalysisImageController aiAnalysisImageController;
  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    aiAnalysisImageController = new AIAnalysisImageController(aiAnalysisImageService);
    mockMvc = MockMvcBuilders.standaloneSetup(aiAnalysisImageController).build();
  }

  @Test
  void 画像情報取得登録画面を表示する() throws Exception {
    mockMvc.perform(get("/ai-analysis-image")).andExpect(status().isOk()).andExpect(view().name("index"));
  }

  @Test
  void 画像情報取得登録を行う() throws Exception {
    ImagePathForm imagePathForm = new ImagePathForm();
    imagePathForm.setImagePath("/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg");

    mockMvc.perform(post("/ai-analysis-image").flashAttr("imagePathForm", imagePathForm)).andExpect(status().isOk())
        .andExpect(model().attribute("errorMessage", ""))
        .andExpect(model().attribute("successMessage", "画像情報取得・登録に成功しました。"))
        .andExpect(model().attribute("imagePathForm", new ImagePathForm())).andExpect(view().name("index"));

    AIAnalysisImageResult aiAnalysisImageResult = new AIAnalysisImageResult();
    aiAnalysisImageResult.setSuccess(true);
    aiAnalysisImageResult.setMessage("success");
    EstimatedData estimatedData = new EstimatedData();
    estimatedData.setClassColumn(3);
    estimatedData.setConfidence(BigDecimal.valueOf(0.8683));
    aiAnalysisImageResult.setEstimatedData(estimatedData);
    aiAnalysisImageResult.setRequestTimestamp(LocalDateTime.now());
    aiAnalysisImageResult.setResponseTimestamp(LocalDateTime.now());

    when(aiAnalysisImageService.getAIAnalysisImageInfo("/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg"))
        .thenReturn(aiAnalysisImageResult);

    verify(aiAnalysisImageService, times(1))
        .getAIAnalysisImageInfo("/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg");

    verify(aiAnalysisImageService, times(1)).saveAIAnalysisLog(any());
  }

  @Test
  void フォーム入力にエラーがある状態で画像情報取得登録を行う() throws Exception {
    ImagePathForm imagePathForm = new ImagePathForm();
    imagePathForm.setImagePath("");

    mockMvc.perform(post("/ai-analysis-image").flashAttr("imagePathForm", imagePathForm)).andExpect(status().isOk())
        .andExpect(model().attribute("errorMessage", "画像パスを入力してください。")).andExpect(view().name("index"));

    verify(aiAnalysisImageService, times(0)).getAIAnalysisImageInfo(any());

    verify(aiAnalysisImageService, times(0)).saveAIAnalysisLog(any());
  }
}
