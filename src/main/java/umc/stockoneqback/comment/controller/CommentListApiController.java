package umc.stockoneqback.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.comment.controller.dto.CustomCommentListResponse;
import umc.stockoneqback.comment.service.CommentListService;
import umc.stockoneqback.global.annotation.ExtractPayload;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentListApiController {
    private final CommentListService commentListService;

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{boardId}")
    public ResponseEntity<CustomCommentListResponse> commentList(@ExtractPayload Long userId,
                                                                 @PathVariable Long boardId,
                                                                 @RequestParam(value = "page", defaultValue = "0", required = false) int page) throws IOException {
        return ResponseEntity.ok(commentListService.getCommentList(userId, boardId, page));
    }
}
