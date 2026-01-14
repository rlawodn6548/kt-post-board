package com.kt.like.repository;

import com.kt.like.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

    @Modifying
    @Query("UPDATE Article a SET a.looks = a.looks + 1 WHERE a.id = :id")
    int increaseLooks(@Param("id")String articleId);
}
