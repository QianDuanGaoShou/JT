package com.guitar.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ChapterResponse {

    private Long id;
    private String title;
    private Integer sortOrder;
    private List<VideoResponse> videos;
}
