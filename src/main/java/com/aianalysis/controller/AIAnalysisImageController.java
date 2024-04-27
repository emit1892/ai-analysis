package com.aianalysis.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aianalysis.form.ImagePathForm;
import com.aianalysis.model.AIAnalysisLog;
import com.aianalysis.repository.AIAnalysisLogRepository;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.transaction.Transactional;
import lombok.Data;

@Controller
@RequestMapping("ai-analysis-image")
public class AIAnalysisImageController {

  @Autowired
  AIAnalysisLogRepository aiAnalysisLogRepository;

  private final RestTemplate restTemplate = new RestTemplate();

  @GetMapping("")
  public String show(@ModelAttribute ImagePathForm form, Model model) {
//    model.addAttribute("imagePathForm", new ImagePathForm());
//    model.addAttribute("successMessage", model.getAttribute("successMessage"));

    return "index";
  }

  @PostMapping("")
  public String create(@ModelAttribute @Validated ImagePathForm form, BindingResult bindingResult, Model model,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      model.addAttribute("errorMessage", "画像パスを入力してください。");
      return show(form, model);
    }

    // 特定の画像ファイルへのパス
    String imagePath = form.getImagePath();

    // APIリクエストURL
    String url = "http://127.0.0.1:3000/success";

    Map<String, String> requestBody = new HashMap<String, String>();
    requestBody.put("image_path", imagePath);

    try {

      // APIリクエスト
      // リクエストタイムスタンプ
      LocalDateTime requestTimestamp = LocalDateTime.now();
      AiAnalysisResult response = restTemplate.postForObject(url, requestBody, AiAnalysisResult.class);

      // レスポンスタイムスタンプ
      LocalDateTime responseTimestamp = LocalDateTime.now();

      if (!response.isSuccess()) {
        // リクエストが失敗
        System.out.println("リクエストが失敗しました。");
      }

      // データ登録
      AIAnalysisLog aiAnalysisLog = new AIAnalysisLog();
      aiAnalysisLog.setImagePath(imagePath);
      aiAnalysisLog.setSuccess(booleanToInteger(response.isSuccess()));
      aiAnalysisLog.setMessage(response.getMessage());
      aiAnalysisLog.setClassColumn(response.getEstimatedData().getClassColumn());
      aiAnalysisLog.setConfidence(response.getEstimatedData().getConfidence());
      aiAnalysisLog.setRequestTimestamp(requestTimestamp);
      aiAnalysisLog.setResponseTimestamp(responseTimestamp);

      createAIAnalysisLog(aiAnalysisLog);

    } catch (Exception e) {
      System.out.println(e);
      model.addAttribute("errorMessage", "画像情報取得・登録に失敗しました。");
      return show(form, model);
    }

    model.addAttribute("errorMessage", "");
    model.addAttribute("successMessage", "画像情報取得・登録に成功しました。");
    model.addAttribute("imagePathForm", new ImagePathForm());
    return show(form, model);
  }

  @Transactional
  public AIAnalysisLog createAIAnalysisLog(AIAnalysisLog aiAnalysisLog) {
    return aiAnalysisLogRepository.save(aiAnalysisLog);
  }

  public Integer booleanToInteger(Boolean value) {
    return value ? 1 : 0;
  }

  public BigDecimal integerToBigdecimal(Integer value) {
    if (Objects.isNull(value)) {
      return null;
    }

    return BigDecimal.valueOf(value);
  }
}

@Data
class AiAnalysisResult {
  private boolean success;
  private String message;

  @JsonProperty("estimated_data")
  private EstimatedData estimatedData;
}

@Data
class EstimatedData {
  @JsonProperty("class")
  private Integer classColumn;

  private BigDecimal confidence;
}
