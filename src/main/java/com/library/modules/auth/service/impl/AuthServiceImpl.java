package com.library.modules.auth.service.impl;

import com.library.common.exception.BusinessException;
import com.library.common.security.JwtTokenProvider;
import com.library.common.security.RedisTokenService;
import com.library.dto.LoginRequest;
import com.library.modules.auth.service.AuthService;
import com.library.modules.user.entity.User;
import com.library.modules.user.service.UserService;
import com.library.vo.LoginUserVO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenService redisTokenService;
    private final UserService userService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           RedisTokenService redisTokenService,
                           UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTokenService = redisTokenService;
        this.userService = userService;
    }

    @Override
    public LoginUserVO login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userService.getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        redisTokenService.saveToken(token, user.getId(), jwtTokenProvider.getExpiration());
        return new LoginUserVO(user.getId(), user.getUsername(), user.getRealName(), user.getRole(), token);
    }

    @Override
    public void logout(String token) {
        redisTokenService.delete(token);
    }
}
