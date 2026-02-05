package com.kt.login.controller;

import com.kt.login.dto.LoginDto;
import com.kt.login.dto.UserRegistrationDto;
import com.kt.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private LoginService loginService;



    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto dto) {
        loginService.register(dto.getUsername(), dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(loginService.login(dto.getUsername(), dto.getPassword()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String refreshToken) {
        loginService.logout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody LoginDto dto) {
        loginService.updateUserPassword(dto.getUsername(), dto.getPassword());
        return ResponseEntity.ok("Update Password successfully");
    }
}
