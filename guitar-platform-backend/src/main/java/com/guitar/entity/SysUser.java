package com.guitar.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String email;

    private String bio;

    private String role; // STUDENT, TEACHER, ADMIN

    private BigDecimal balance;

    private String qualificationUrl;

    private String auditStatus; // PENDING, APPROVED, REJECTED

    private String auditReason;

    private Boolean enabled;

    private Integer teachingExperience;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
