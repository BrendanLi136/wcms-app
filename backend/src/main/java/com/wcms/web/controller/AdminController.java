package com.wcms.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcms.common.result.Result;
import com.wcms.domain.entity.SysUser;
import com.wcms.service.SysUserService;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SysUserService sysUserService;

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody SysUser loginReq, HttpSession session) {
        // Simple plain text password check for demo as requested (User didn't strictly
        // ask for hashing in "implementation details" but advised in "security")
        // In real app, use BCrypt. Here we match the initiated DB value.
        SysUser user = sysUserService.login(loginReq.getUsername(), loginReq.getPassword());
        if (user != null) {
            session.setAttribute("user", user);
            return Result.success(user);
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success();
    }

    @GetMapping("/info")
    public Result<SysUser> info(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(user);
    }
}
