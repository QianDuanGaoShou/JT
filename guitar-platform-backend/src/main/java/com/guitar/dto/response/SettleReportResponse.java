package com.guitar.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleReportResponse {

    private Long teacherId;
    private String teacherName;
    private Integer courseCount;
    private Integer studentCount;
    private BigDecimal totalIncome;
    private BigDecimal platformFee;
    private BigDecimal teacherIncome;
    private String settleStatus;
}
