package com.guitar.service;

import com.guitar.common.PageResult;
import com.guitar.dto.request.CreateChapterRequest;
import com.guitar.dto.request.CreateCourseRequest;
import com.guitar.dto.request.CreateVideoRequest;
import com.guitar.dto.request.UpdateCourseRequest;
import com.guitar.dto.response.IncomeStatsResponse;
import com.guitar.dto.response.TeacherCourseResponse;
import com.guitar.entity.Course;
import com.guitar.entity.CourseChapter;
import com.guitar.entity.CourseVideo;

public interface TeacherService {

    Course createCourse(Long teacherId, CreateCourseRequest request);

    void updateCourse(Long courseId, Long teacherId, UpdateCourseRequest request);

    void deleteCourse(Long courseId, Long teacherId);

    PageResult<TeacherCourseResponse> getMyCourses(Long teacherId, int page, int size, String status);

    CourseChapter addChapter(CreateChapterRequest request, Long teacherId);

    void updateChapter(Long chapterId, Long teacherId, String title, Integer sortOrder);

    void deleteChapter(Long chapterId, Long teacherId);

    CourseVideo addVideo(CreateVideoRequest request, Long teacherId);

    void updateVideo(Long videoId, Long teacherId, String title, Integer sortOrder);

    void deleteVideo(Long videoId, Long teacherId);

    IncomeStatsResponse getIncomeStats(Long teacherId, String startMonth, String endMonth);
}
