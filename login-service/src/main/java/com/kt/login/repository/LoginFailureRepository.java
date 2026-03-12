package com.kt.login.repository;

import com.kt.login.entity.LoginFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginFailureRepository extends JpaRepository<LoginFailure, String> {
}
