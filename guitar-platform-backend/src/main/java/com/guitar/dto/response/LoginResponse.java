package com.guitar.dto.response;

import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserInfoResponse userInfo;
}
