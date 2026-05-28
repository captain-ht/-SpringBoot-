package com.library.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.modules.user.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends IService<User>, UserDetailsService {
    User getByUsername(String username);
}
