package com.bike.module.auth.entity;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String nickname;
}
