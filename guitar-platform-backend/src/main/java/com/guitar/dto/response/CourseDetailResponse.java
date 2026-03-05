package com.guitar.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseDetailResponse extends CourseListItemResponse {

    private String description;
    private List<ChapterResponse> chapters;
    private UserInfoResponse teacherInfo;
    private Map<String, Object> studyProgress;
}
