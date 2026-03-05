package com.guitar.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("income_record")
public class IncomeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teacherId;

    private Long courseId;

    private Long orderId;

    private BigDecimal amount;

    private String type; // COURSE_PURCHASE, SETTLEMENT

    private String settleMonth;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
