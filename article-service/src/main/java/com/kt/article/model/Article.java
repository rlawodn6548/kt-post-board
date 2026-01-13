package com.kt.article.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString @Builder
@Entity @EntityListeners(AuditingEntityListener.class)
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
    
    @Column(name = "author_id", nullable=false)
    private String authorId;

    @CreatedDate
    @Column(name="create_time", nullable=false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(name="modified_time", nullable = false)
    private LocalDateTime modifiedTime;
}
