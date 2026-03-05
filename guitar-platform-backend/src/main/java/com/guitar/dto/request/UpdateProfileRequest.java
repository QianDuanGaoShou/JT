package com.guitar.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String nickname;
    private String avatar;
    private String bio;
}
