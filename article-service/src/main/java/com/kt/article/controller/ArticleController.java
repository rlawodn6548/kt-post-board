package com.kt.article.controller;

import com.kt.article.model.Article;
import com.kt.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService service;

    @GetMapping
    public List<Article> getAllArticle() {
        return service.selectAll();
    }

    @GetMapping("/{id}")
    public Article getArticleById(@PathVariable String id) {
        return service.selectById(id).orElse(null);
    }

    @PostMapping()
    public Article updateArticle(@RequestBody Article article) {
        return service.updateArticle(article);
    }

    @DeleteMapping("/{id}")
    public void deleteArticleById(@PathVariable String id) {
        service.deleteArticleById(id);
    }
}
