package com.library.modules.auth.service;

import com.library.dto.LoginRequest;
import com.library.vo.LoginUserVO;

public interface AuthService {
    LoginUserVO login(LoginRequest request);
    void logout(String token);
}
