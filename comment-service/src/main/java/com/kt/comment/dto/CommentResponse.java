package com.kt.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private String articleId;
    private String content;
    private String author;
    private LocalDateTime createTime;
}
