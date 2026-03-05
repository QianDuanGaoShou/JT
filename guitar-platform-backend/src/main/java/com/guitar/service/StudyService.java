package com.guitar.service;

import com.guitar.common.PageResult;
import com.guitar.dto.response.StudyRecordResponse;

import java.util.Map;

public interface StudyService {

    PageResult<StudyRecordResponse> getStudyRecords(Long studentId, int page, int size);

    void updateProgress(Long studentId, Long courseId, Long videoId, Integer progress, Integer lastPosition);

    Map<String, Object> getStudyStats(Long studentId);
}
