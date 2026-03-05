package com.guitar.dto.response;

import lombok.Data;

@Data
public class VideoResponse {

    private Long id;
    private String title;
    private String videoUrl;
    private Integer duration;
    private Integer sortOrder;
}
