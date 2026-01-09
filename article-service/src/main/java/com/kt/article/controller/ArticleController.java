package com.kt.article.controller;

import com.kt.article.dto.ArticleRequest;
import com.kt.article.model.Article;
import com.kt.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article") // 공통 경로 설정
public class ArticleController {
    @Autowired
    private ArticleService articleService;



    // 게시글 생성
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody ArticleRequest request) {
        Article created = articleService.createArticle(
                request.getTitle(),
                request.getContent(),
                request.getAuthor()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable String id) {
        Article article = articleService.getArticle(id);
        return ResponseEntity.ok(article);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable String id,
            @RequestBody ArticleRequest request) {
        Article updated = articleService.updateArticle(id, request.getTitle(), request.getContent());
        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }
}