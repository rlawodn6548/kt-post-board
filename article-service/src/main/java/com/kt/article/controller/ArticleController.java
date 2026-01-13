package com.kt.article.controller;

import com.kt.article.dto.ArticleRequest;
import com.kt.article.model.Article;
import com.kt.article.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/article") // 공통 경로 설정
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);


    // 게시글 생성
    @PostMapping
    public ResponseEntity<Article> createArticle(@AuthenticationPrincipal Jwt jwt, @RequestBody ArticleRequest request) {
        Article created = articleService.createArticle(
                request.getTitle(),
                request.getContent(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getSubject()
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
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ArticleRequest request) {
        Article updated = null;

        if (isAuthorization(id, jwt)) {
            updated = articleService.updateArticle(id, request.getTitle(), request.getContent());
        }
        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable String id,
                                              @AuthenticationPrincipal Jwt jwt) {
        if (isAuthorization(id, jwt)) {
            articleService.deleteArticle(id);
        }
        return ResponseEntity.noContent().build();
    }

    private boolean isAuthorization(String articleId, Jwt jwt) {
        Article article = articleService.getArticle(articleId);
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> roles = (List<String>) realmAccess.get("roles");
        String currentUserId = jwt.getSubject();

        if (logger.isInfoEnabled()) {
            logger.info("[ArticleController] articleId : {}, authorId : {}, userId : {}, role : {}", articleId, article.getAuthorId(), currentUserId, roles.toString());
        }

        boolean isAdmin = roles.contains("kt-admin");
        boolean isUser = roles.contains("kt-user");
        boolean isAuthor = article.getAuthorId().equals(currentUserId);

        if (isAdmin || (isUser && isAuthor)) {
            return true;
        }
        throw new AccessDeniedException("수정 권한이 없습니다. (관리자 또는 작성자만 가능)");
    }
}