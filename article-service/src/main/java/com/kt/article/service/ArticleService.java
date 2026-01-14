package com.kt.article.service;

import com.kt.article.message.MessageSourceBean;
import com.kt.article.model.Article;
import com.kt.article.repository.ArticleRepository;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class ArticleService {
    @Autowired
    private ArticleRepository repository;

    @Autowired
    private MessageSourceBean messageSource;

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);



    // 게시글 작성
    @Transactional
    public Article createArticle(String title, String content, String author, String authorId) {
        if (logger.isInfoEnabled()) {
            logger.info("Create Article - {}", content);
        }
        Article article = Article.builder()
                    .id(UUID.randomUUID().toString())
                    .title(title)
                    .content(content)
                    .author(author)
                    .authorId(authorId)
                    .build();
        return repository.save(article);
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        return repository.findAll();
    }

    // 특정 게시글 상세 조회
    @Transactional(readOnly = true)
    public Article getArticle(String id) {
        messageSource.publishArticleMessage("article", id);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));
    }

    // 게시글 수정 (Dirty Checking 활용)
    @Transactional
    public Article updateArticle(String id, String newTitle, String newContent) {
        Article article = getArticle(id);
        article.setTitle(newTitle);
        article.setContent(newContent);
        repository.save(article);
        return article;
    }

    // 게시글 삭제
    @Transactional
    public void deleteArticle(String id) {
        Article article = getArticle(id);
        repository.delete(article);
    }
}
