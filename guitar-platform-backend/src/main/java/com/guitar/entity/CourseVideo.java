package com.guitar.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_video")
public class CourseVideo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long chapterId;

    private String title;

    private String videoUrl;

    private Integer duration;

    private Integer sortOrder;

    private Long fileSize;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
