package com.guitar.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {

    private String orderNo;
    private Long courseId;
    private String courseTitle;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
