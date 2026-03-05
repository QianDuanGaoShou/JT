package com.guitar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guitar.entity.CourseVideo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseVideoMapper extends BaseMapper<CourseVideo> {

    @Select("SELECT * FROM course_video WHERE chapter_id = #{chapterId} ORDER BY sort_order ASC")
    List<CourseVideo> findByChapterId(@Param("chapterId") Long chapterId);

    @Select("SELECT cv.* FROM course_video cv " +
            "JOIN course_chapter cc ON cv.chapter_id = cc.id " +
            "WHERE cc.course_id = #{courseId}")
    List<CourseVideo> findByCourseId(@Param("courseId") Long courseId);

    @Select("SELECT COALESCE(SUM(cv.duration), 0) FROM course_video cv " +
            "JOIN course_chapter cc ON cv.chapter_id = cc.id " +
            "WHERE cc.course_id = #{courseId}")
    Integer sumDurationByCourseId(@Param("courseId") Long courseId);
}
