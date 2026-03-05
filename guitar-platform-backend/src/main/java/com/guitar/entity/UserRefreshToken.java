package com.guitar.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_refresh_token")
public class UserRefreshToken {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String refreshToken;

    private LocalDateTime expiresAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
