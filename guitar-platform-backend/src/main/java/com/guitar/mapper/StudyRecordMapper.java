package com.guitar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guitar.dto.response.StudyRecordResponse;
import com.guitar.entity.StudyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudyRecordMapper extends BaseMapper<StudyRecord> {

    @Select("SELECT * FROM study_record WHERE student_id = #{studentId} AND course_id = #{courseId} AND video_id = #{videoId} LIMIT 1")
    StudyRecord findByStudentCourseVideo(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("videoId") Long videoId
    );

    List<StudyRecordResponse> selectStudyRecords(
            @Param("studentId") Long studentId,
            @Param("offset") long offset,
            @Param("size") int size
    );

    @Select("SELECT COUNT(DISTINCT course_id) FROM study_record WHERE student_id = #{studentId}")
    int countDistinctCoursesByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT AVG(sr.progress) FROM study_record sr WHERE sr.student_id = #{studentId} AND sr.course_id = #{courseId}")
    Double avgProgressByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    long countStudyRecords(@Param("studentId") Long studentId);
}
