package com.internetbanking.controller;

import com.internetbanking.dto.UserDto;
import com.internetbanking.entity.User;
import com.internetbanking.request.UserRequest;
import com.internetbanking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getAll(@PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserRequest request) {
        UserDto user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody UserRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("forgot-password")
    public ResponseEntity<?> forgotPassword() {
        userService.sendOTPForgotPassword();
        return ResponseEntity.ok().build();
    }

    @PostMapping("forgot-password/{otpValue}")
    public ResponseEntity<?> forgotPassword(@PathVariable("otpValue") Integer otpValue, @RequestBody UserRequest request) {
        userService.validateForgotPassword(otpValue, request);
        return ResponseEntity.ok().build();
    }
}
