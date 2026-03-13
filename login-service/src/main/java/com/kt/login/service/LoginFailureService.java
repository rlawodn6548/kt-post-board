package com.kt.login.service;

import com.kt.login.entity.LoginFailure;
import com.kt.login.repository.LoginFailureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginFailureService {

    private final LoginFailureRepository loginFailureRepository;

    @Transactional
    public int incrementFailure(String userId) {
        LoginFailure failure = loginFailureRepository.findById(userId)
                .orElse(LoginFailure.builder()
                        .userId(userId)
                        .failureCount(0)
                        .build());

        failure.setFailureCount(failure.getFailureCount() + 1);
        failure.setLastFailureTime(LocalDateTime.now());
        
        loginFailureRepository.save(failure);
        return failure.getFailureCount();
    }

    @Transactional
    public void resetFailure(String userId) {
        loginFailureRepository.deleteById(userId);
    }

    public int getFailureCount(String userId) {
        return loginFailureRepository.findById(userId)
                .map(LoginFailure::getFailureCount)
                .orElse(0);
    }

    public java.util.List<LoginFailure> getAllFailures() {
        return loginFailureRepository.findAll();
    }
}
