package com.guitar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCourseRequest {

    @NotBlank(message = "课程标题不能为空")
    private String title;

    @NotBlank(message = "课程分类不能为空")
    private String category;

    @NotNull(message = "课程价格不能为空")
    private BigDecimal price;

    private String coverUrl;

    private String description;
}
