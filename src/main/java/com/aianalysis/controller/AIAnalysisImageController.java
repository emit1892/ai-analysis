package com.aianalysis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aianalysis.form.ImagePathForm;
import com.aianalysis.model.AIAnalysisImageResult;
import com.aianalysis.service.AIAnalysisImageService;

@Controller
@RequestMapping("ai-analysis-image")
public class AIAnalysisImageController {

  private final AIAnalysisImageService aiAnalysisImageService;

  public AIAnalysisImageController(AIAnalysisImageService aiAnalysisImageService) {
    this.aiAnalysisImageService = aiAnalysisImageService;
  }

  @GetMapping("")
  public String show(@ModelAttribute ImagePathForm form, Model model) {
    return "index";
  }

  @PostMapping("")
  public String create(@ModelAttribute @Validated ImagePathForm form, BindingResult bindingResult, Model model,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      model.addAttribute("errorMessage", "画像パスを入力してください。");
      return show(form, model);
    }

    // 画像ファイルのパス取得
    String imagePath = form.getImagePath();

    try {
      // 画像所属クラスを取得
      AIAnalysisImageResult aiAnalysisImageResult = aiAnalysisImageService.getAIAnalysisImageInfo(imagePath);

      // データ保存
      aiAnalysisImageService.saveAIAnalysisLog(aiAnalysisImageResult);

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
}
