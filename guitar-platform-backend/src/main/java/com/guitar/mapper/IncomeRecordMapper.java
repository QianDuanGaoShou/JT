package com.guitar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guitar.entity.IncomeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface IncomeRecordMapper extends BaseMapper<IncomeRecord> {

    @Select("SELECT COALESCE(SUM(amount), 0) FROM income_record WHERE teacher_id = #{teacherId}")
    BigDecimal sumTotalByTeacherId(@Param("teacherId") Long teacherId);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM income_record WHERE teacher_id = #{teacherId} AND settle_month = #{month}")
    BigDecimal sumByTeacherIdAndMonth(@Param("teacherId") Long teacherId, @Param("month") String month);

    @Select("SELECT settle_month AS month, SUM(amount) AS income, COUNT(DISTINCT order_id) AS studentCount " +
            "FROM income_record WHERE teacher_id = #{teacherId} " +
            "AND settle_month >= #{startMonth} AND settle_month <= #{endMonth} " +
            "GROUP BY settle_month ORDER BY settle_month")
    List<Map<String, Object>> selectMonthlyStats(
            @Param("teacherId") Long teacherId,
            @Param("startMonth") String startMonth,
            @Param("endMonth") String endMonth
    );

    @Select("SELECT COALESCE(SUM(amount), 0) FROM income_record WHERE teacher_id = #{teacherId} AND settle_month = #{month}")
    BigDecimal sumAmountByTeacherAndMonth(@Param("teacherId") Long teacherId, @Param("month") String month);
}
