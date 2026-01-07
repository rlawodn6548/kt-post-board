package com.kt.article.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter @Setter @ToString
@Entity
@Table(name="article")
public class Article {
    @Id
    @Column(name="id", nullable = false)
    private String id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="content")
    private String content;

    @Column(name = "author", nullable=false)
    private String author;

    @Column(name="createtime", nullable=false)
    private LocalDateTime createTime;

    @Column(name="modifiedtime", nullable = false)
    private LocalDateTime modifiedTime;
}
