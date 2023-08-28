package umc.stockoneqback.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.comment.controller.dto.CommentRequest;
import umc.stockoneqback.comment.service.CommentService;
import umc.stockoneqback.global.annotation.ExtractPayload;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentApiController {
    private final CommentService commentService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{boardId}")
    public ResponseEntity<Void> create(@ExtractPayload Long writerId, @PathVariable Long boardId,
                                       @RequestPart(value = "request")  @Valid CommentRequest request,
                                       @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        commentService.create(writerId, boardId, multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> update(@ExtractPayload Long writerId, @PathVariable Long commentId,
                                       @RequestPart(value = "request")  @Valid CommentRequest request,
                                       @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        commentService.update(writerId, commentId, multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@ExtractPayload Long writerId, @PathVariable Long commentId) {
        commentService.delete(writerId, commentId);
        return ResponseEntity.ok().build();
    }
}
