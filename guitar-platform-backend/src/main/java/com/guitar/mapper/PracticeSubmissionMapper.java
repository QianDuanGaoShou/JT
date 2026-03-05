package com.guitar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guitar.entity.PracticeSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PracticeSubmissionMapper extends BaseMapper<PracticeSubmission> {

    @Select("SELECT COUNT(*) FROM practice_submission ps " +
            "JOIN course c ON ps.course_id = c.id " +
            "WHERE c.teacher_id = #{teacherId} AND ps.status = 'PENDING'")
    int countPendingByTeacherId(@Param("teacherId") Long teacherId);
}
