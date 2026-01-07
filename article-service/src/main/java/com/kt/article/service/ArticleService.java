package com.kt.article.service;

import com.kt.article.model.Article;
import com.kt.article.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository repository;

    public List<Article> selectAll() {
        Iterable<Article> all = repository.findAll();
        if (!all.iterator().hasNext()) {
            return null;
        }
        return StreamSupport.stream(all.spliterator(),false).collect(Collectors.toList());
    }

    public Optional<Article> selectById(String id) {
        return repository.findById(id);
    }

    public Article updateArticle(Article article) {
        LocalDateTime now = LocalDateTime.now();

        if (article.getCreateTime() == null) {
            article.setCreateTime(now);
        }
        article.setModifiedTime(now);
        return repository.save(article);
    }

    public void deleteArticleById(String id) {
        repository.deleteById(id);
    }
}
