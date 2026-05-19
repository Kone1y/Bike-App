package com.bike.module.app.controller;

import com.bike.common.exception.BusinessException;
import com.bike.common.result.Result;
import com.bike.common.utils.JwtUtils;
import com.bike.mapper.SysUserMapper;
import com.bike.module.auth.entity.LoginVO;
import com.bike.module.auth.entity.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/app/auth")
@RequiredArgsConstructor
public class AppAuthController {

    private final SysUserMapper sysUserMapper;
    private final JwtUtils jwtUtils;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    @Value("${wechat.dev-mode:false}")
    private boolean devMode;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null || code.isBlank()) {
            throw new BusinessException("code不能为空");
        }

        String openid;
        if (devMode) {
            openid = "mock_" + code;
        } else {
            openid = getOpenidFromWechat(code);
        }

        SysUser user = sysUserMapper.findByOpenid(openid);
        if (user == null) {
            user = new SysUser();
            user.setUsername("wx_" + System.currentTimeMillis());
            user.setPassword(passwordEncoder.encode(""));
            user.setNickname("微信用户");
            user.setUserType(1);
            user.setStatus(0);
            user.setOpenid(openid);
            sysUserMapper.insert(user);
        }

        if (user.getStatus() == 1) {
            throw new BusinessException("账户已被禁用");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getUserType());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        return Result.success(vo);
    }

    private String getOpenidFromWechat(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code);
        try {
            RestTemplate restTemplate = new RestTemplate();
            @SuppressWarnings("unchecked")
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);
            String openid = (String) result.get("openid");
            if (openid == null || openid.isBlank()) {
                throw new BusinessException("微信登录失败：" + result.get("errmsg"));
            }
            return openid;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("微信登录失败，请稍后重试");
        }
    }
}
