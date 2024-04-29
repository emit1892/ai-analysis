package com.aianalysis.form;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest
public class ImagePathFormTest {

  @Autowired
  private Validator validator;

  private ImagePathForm imagePathForm;

  @BeforeEach
  public void setup() {
    imagePathForm = new ImagePathForm();
  }

  @Test
  void 画像パスが入力されている場合エラーメッセージが表示されない() throws Exception {
    imagePathForm.setImagePath("/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg");
    Set<ConstraintViolation<ImagePathForm>> result = validator.validate(imagePathForm);

    assertThat(result.size()).isEqualTo(0);
  }

  @Test
  void 画像パスが入力されていない場合エラーメッセージが表示される() throws Exception {
    imagePathForm.setImagePath("");
    Set<ConstraintViolation<ImagePathForm>> result = validator.validate(imagePathForm);

    assertThat(result).extracting("message").containsOnly("画像のパスは必須です。");
  }

}
