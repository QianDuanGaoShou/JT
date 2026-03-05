package com.guitar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateVideoRequest {

    @NotNull(message = "章节ID不能为空")
    private Long chapterId;

    @NotBlank(message = "视频标题不能为空")
    private String title;

    private String videoUrl;

    private Integer duration;

    private Integer sortOrder;

    private Long fileSize;
}
