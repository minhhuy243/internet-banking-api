package com.internetbanking.controller;

import com.internetbanking.request.LoginRequest;
import com.internetbanking.request.RefreshTokenRequest;
import com.internetbanking.response.LoginResponse;
import com.internetbanking.response.RefreshTokenResponse;
import com.internetbanking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> getNewToken(@RequestBody @Valid RefreshTokenRequest request) {
        RefreshTokenResponse response = userService.getNewToken(request);
        return ResponseEntity.ok(response);
    }
}
