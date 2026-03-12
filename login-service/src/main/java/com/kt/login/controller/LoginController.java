package com.kt.login.controller;

import com.kt.login.dto.LoginDto;
import com.kt.login.dto.UserRegistrationDto;
import com.kt.login.exception.LoginFailedException;
import com.kt.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Map<String, Object>> handleLoginFailedException(LoginFailedException e) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("message", e.getMessage());
        errorBody.put("failureCount", e.getFailureCount());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto dto) {
        loginService.register(dto.getUsername(), dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto dto) {
        Map<String, Object> result = loginService.login(dto.getUsername(), dto.getPassword());
        
        // refresh_token 추출 및 쿠키 설정
        String refreshToken = (String) result.get("refresh_token");
        
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // 로컬 테스트 환경이므로 false, 운영에서는 true 권장
                .path("/")
                .maxAge(60 * 60 * 24 * 7) // 7일
                .sameSite("Lax")
                .build();

        // 보안을 위해 응답 바디에서는 refresh_token 제거
        result.remove("refresh_token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Map<String, Object> result = loginService.refreshToken(refreshToken);
            
            if (result.containsKey("refresh_token")) {
                String newRefreshToken = (String) result.get("refresh_token");
                ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(60 * 60 * 24 * 7)
                        .sameSite("Lax")
                        .build();
                
                result.remove("refresh_token");
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(result);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken != null) {
            loginService.logout(refreshToken);
        }
        
        // 쿠키 만료시켜 제거
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody LoginDto dto) {
        loginService.updateUserPassword(dto.getUsername(), dto.getPassword());
        return ResponseEntity.ok("Update Password successfully");
    }
}
