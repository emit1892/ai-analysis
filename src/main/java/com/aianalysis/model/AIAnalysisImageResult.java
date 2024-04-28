package com.aianalysis.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AIAnalysisImageResult {
  @JsonIgnore
  private String imagePath;

  private boolean success;

  private String message;

  @JsonProperty("estimated_data")
  private EstimatedData estimatedData;

  @JsonIgnore
  private LocalDateTime requestTimestamp;

  @JsonIgnore
  private LocalDateTime responseTimestamp;
}
