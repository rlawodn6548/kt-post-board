package com.kt.comment.service;

import com.kt.comment.model.Comment;
import com.kt.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    private CommentRepository repository;



    // 댓글 생성
    @Transactional
    public Comment createComment(String articleId, String content, String author) {
        Comment comment = Comment.builder() // @Builder가 엔티티에 있다고 가정
                .id(UUID.randomUUID().toString()) // String ID인 경우 보통 UUID 생성
                .articleId(articleId)
                .content(content)
                .author(author)
                .build();

        return repository.save(comment);
    }

    // 댓글 단건 조회
    @Transactional(readOnly = true)
    public Comment getComment(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id=" + id));
    }

    // 특정 게시글의 모든 댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByArticle(String articleId) {
        return repository.findByArticleId(articleId);
    }

    // 댓글 수정
    @Transactional
    public Comment updateComment(String id, String newContent) {
        Comment comment = getComment(id);
        comment.setContent(newContent);

        repository.save(comment);
        return comment;
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(String id) {
        Comment comment = getComment(id);
        repository.delete(comment);
    }
}
