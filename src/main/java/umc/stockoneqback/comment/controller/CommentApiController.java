package umc.stockoneqback.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{boardId}")
    public ResponseEntity<Void> create(@ExtractPayload Long writerId, @PathVariable Long boardId,
                                       @RequestBody @Valid CommentRequest request,
                                       @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        commentService.create(writerId, boardId, multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> update(@ExtractPayload Long writerId, @PathVariable Long commentId,
                                       @RequestBody @Valid CommentRequest request,
                                       @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        commentService.update(writerId, commentId, multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@ExtractPayload Long writerId, @PathVariable Long commentId) {
        commentService.delete(writerId, commentId);
        return ResponseEntity.ok().build();
    }
}
