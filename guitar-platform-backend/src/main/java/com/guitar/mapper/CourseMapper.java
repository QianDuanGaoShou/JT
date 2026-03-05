package com.guitar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guitar.dto.response.CourseListItemResponse;
import com.guitar.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    IPage<CourseListItemResponse> selectCourseList(
            Page<CourseListItemResponse> page,
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("currentUserId") Long currentUserId,
            @Param("status") String status
    );

    CourseListItemResponse selectCourseDetail(
            @Param("courseId") Long courseId,
            @Param("currentUserId") Long currentUserId
    );
}
