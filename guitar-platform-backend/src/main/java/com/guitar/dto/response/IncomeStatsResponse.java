package com.guitar.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class IncomeStatsResponse {

    private BigDecimal totalIncome;
    private BigDecimal currentMonthIncome;
    private Integer studentCount;
    private Integer pendingGradeCount;
    private List<MonthlyIncomeData> monthlyData;
    private Map<String, Object> chartData;
}
