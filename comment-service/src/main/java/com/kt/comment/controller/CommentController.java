package com.kt.comment.controller;

import com.kt.comment.dto.CommentRequest;
import com.kt.comment.dto.CommentResponse;
import com.kt.comment.model.Comment;
import com.kt.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request) {
        Comment comment = commentService.createComment(
                request.getArticleId(),
                request.getContent(),
                request.getAuthor()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(comment));
    }

    // 특정 게시글의 모든 댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByArticle(@RequestParam String articleId) {
        List<CommentResponse> responses = commentService.getCommentsByArticle(articleId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable String id,
            @RequestBody String newContent) { // 간단하게 내용만 받을 경우
        Comment updated = commentService.updateComment(id, newContent);
        return ResponseEntity.ok(convertToResponse(updated));
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // 변환 헬퍼 메소드
    private CommentResponse convertToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getArticleId(),
                comment.getContent(),
                comment.getAuthor(),
                comment.getCreateTime()
        );
    }
}
