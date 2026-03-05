package com.guitar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guitar.entity.CourseChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapter> {

    @Select("SELECT * FROM course_chapter WHERE course_id = #{courseId} ORDER BY sort_order ASC")
    List<CourseChapter> findByCourseId(@Param("courseId") Long courseId);
}
