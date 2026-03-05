package com.guitar.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProgressRequest {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "视频ID不能为空")
    private Long videoId;

    @NotNull(message = "进度不能为空")
    private Integer progress;

    private Integer lastPosition;
}
