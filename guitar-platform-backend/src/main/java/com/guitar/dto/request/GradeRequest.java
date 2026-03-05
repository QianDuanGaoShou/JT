package com.guitar.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradeRequest {

    @NotNull(message = "评分不能为空")
    private BigDecimal totalScore;

    private String feedback;
}
