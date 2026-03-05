package com.guitar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guitar.entity.UserRefreshToken;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRefreshTokenMapper extends BaseMapper<UserRefreshToken> {

    @Select("SELECT * FROM user_refresh_token WHERE refresh_token = #{refreshToken} LIMIT 1")
    UserRefreshToken findByRefreshToken(@Param("refreshToken") String refreshToken);

    @Delete("DELETE FROM user_refresh_token WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM user_refresh_token WHERE refresh_token = #{refreshToken}")
    int deleteByRefreshToken(@Param("refreshToken") String refreshToken);
}
