package com.guitar.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_record")
public class StudyRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long videoId;

    private Integer progress;

    private Integer lastPosition;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
