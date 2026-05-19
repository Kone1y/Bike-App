package com.bike.module.auth.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String avatar;
    private String openid;
    private Integer userType;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
