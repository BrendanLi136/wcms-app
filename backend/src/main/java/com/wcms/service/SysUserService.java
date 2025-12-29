package com.wcms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcms.domain.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser login(String username, String password);
}
