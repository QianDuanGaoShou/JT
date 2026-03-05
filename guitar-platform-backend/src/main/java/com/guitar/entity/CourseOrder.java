package com.guitar.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("course_order")
public class CourseOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long studentId;

    private Long courseId;

    private BigDecimal amount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
