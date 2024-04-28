package com.aianalysis.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EstimatedData {
  @JsonProperty("class")
  private Integer classColumn;

  private BigDecimal confidence;
}
