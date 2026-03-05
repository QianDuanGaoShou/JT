package com.guitar.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyIncomeData {

    private String month;
    private BigDecimal income;
    private Integer studentCount;
}
