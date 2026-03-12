package com.kt.login.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_failures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginFailure {

    @Id
    @Column(name = "userid")
    private String userId;

    @Column(name = "failurecount")
    private int failureCount;

    @Column(name = "lastfailuretime")
    private LocalDateTime lastFailureTime;
}
