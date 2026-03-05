package com.guitar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guitar.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} LIMIT 1")
    SysUser findByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username}")
    int countByUsername(@Param("username") String username);
}
