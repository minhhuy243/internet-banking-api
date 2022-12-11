package com.internetbanking.controller;

import com.internetbanking.entity.User;
import com.internetbanking.request.UserRequest;
import com.internetbanking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody UserRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}
