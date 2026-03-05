package com.guitar.dto.response;

import lombok.Data;

@Data
public class UserInfoResponse {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
    private java.math.BigDecimal balance;
    private String auditStatus;
    private String bio;
    private Integer teachingExperience;
    private String email;
    private Boolean enabled;
    private String qualificationUrl;
}
