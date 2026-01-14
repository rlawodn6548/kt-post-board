package com.kt.comment.controller;

import com.kt.comment.dto.CommentRequest;
import com.kt.comment.dto.CommentResponse;
import com.kt.comment.model.Comment;
import com.kt.comment.service.CommentService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);


    // 댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@AuthenticationPrincipal Jwt jwt, @RequestBody CommentRequest request) {
        Comment comment = commentService.createComment(
                request.getArticleId(),
                request.getContent(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getSubject()
        );

        if (logger.isInfoEnabled()) {
            logger.info("Create Comment {} by {}", comment.getId() , comment.getAuthor());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(comment));
    }

    // 특정 게시글의 모든 댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByArticle(@RequestParam String articleId) {
        if (logger.isInfoEnabled()) {
            logger.info("Get Comments by Article {}", articleId);
        }

        List<CommentResponse> responses = commentService.getCommentsByArticle(articleId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id,
            @RequestBody String newContent) { // 간단하게 내용만 받을 경우
        if (logger.isInfoEnabled()) {
            logger.info("Update Comments {}", id);
        }

        Comment updated = null;
        if (isAuthorization(id, jwt)) {
            updated = commentService.updateComment(id, newContent);
        }
        return ResponseEntity.ok(convertToResponse(updated));
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal Jwt jwt,
                                              @PathVariable String id) {
        if (logger.isInfoEnabled()) {
            logger.info("Delete Comments {}", id);
        }

        if (isAuthorization(id, jwt)) {
            commentService.deleteComment(id);
        }
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

    private boolean isAuthorization(String commentId, Jwt jwt) {
        Comment comment = commentService.getComment(commentId);
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> roles = (List<String>) realmAccess.get("roles");
        String currentUserId = jwt.getSubject();

        if (logger.isInfoEnabled()) {
            logger.info("[CommentController] commentId : {}, authorId : {}, userId : {}, role : {}", commentId, comment.getAuthorId(), currentUserId, roles.toString());
        }

        boolean isAdmin = roles.contains("kt-admin");
        boolean isUser = roles.contains("kt-user");
        boolean isAuthor = comment.getAuthorId().equals(currentUserId);

        if (isAdmin || (isUser && isAuthor)) {
            return true;
        }
        throw new AccessDeniedException("수정 권한이 없습니다. (관리자 또는 작성자만 가능)");
    }
}
