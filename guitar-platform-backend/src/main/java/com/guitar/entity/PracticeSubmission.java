package com.guitar.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("practice_submission")
public class PracticeSubmission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    private String videoUrl;

    private BigDecimal totalScore;

    private String teacherFeedback;

    private String status; // PENDING, GRADED

    private LocalDateTime gradedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
