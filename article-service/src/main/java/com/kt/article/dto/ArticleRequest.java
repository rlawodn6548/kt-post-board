package com.kt.article.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArticleRequest {
    private String title;
    private String content;
    private String author;
}
