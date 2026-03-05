package com.guitar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guitar.entity.CourseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseOrderMapper extends BaseMapper<CourseOrder> {

    @Select("SELECT COUNT(*) FROM course_order WHERE student_id = #{studentId} AND course_id = #{courseId}")
    int countByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Select("SELECT COUNT(*) FROM course_order WHERE course_id = #{courseId}")
    int countByCourseId(@Param("courseId") Long courseId);
}
