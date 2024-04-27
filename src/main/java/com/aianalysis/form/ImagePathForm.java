package com.aianalysis.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImagePathForm {
  @NotBlank(message = "画像のパスは必須です。")
  private String imagePath;
}
