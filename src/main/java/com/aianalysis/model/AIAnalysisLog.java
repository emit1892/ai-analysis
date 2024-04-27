package com.aianalysis.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ai_analysis_log")
public class AIAnalysisLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "image_path")
  private String imagePath;

  private Integer success;

  private String message;

  @Column(name = "`class`")
  private Integer classColumn;

  private BigDecimal confidence;

  @Column(name = "request_timestamp")
  private LocalDateTime requestTimestamp;

  @Column(name = "response_timestamp")
  private LocalDateTime responseTimestamp;
}
