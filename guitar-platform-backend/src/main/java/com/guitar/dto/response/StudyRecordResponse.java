package com.guitar.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyRecordResponse {

    private Long id;
    private Long courseId;
    private String courseTitle;
    private String coverUrl;
    private Integer overallProgress;
    private String currentChapter;
    private String currentVideo;
    private Long currentVideoId;
    private Integer lastPosition;
    private LocalDateTime updatedAt;
}
