package umc.stockoneqback.reply.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.reply.controller.dto.ReplyRequest;
import umc.stockoneqback.reply.service.ReplyService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyApiController {
    private final ReplyService replyService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{commentId}")
    public ResponseEntity<Void> create(@ExtractPayload Long writerId, @PathVariable Long commentId,
                                       @RequestPart(value = "request")  @Valid ReplyRequest request,
                                       @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        replyService.create(writerId, commentId, multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{replyId}")
    public ResponseEntity<Void> update(@ExtractPayload Long writerId, @PathVariable Long replyId,
                                       @RequestPart(value = "request")  @Valid ReplyRequest request,
                                       @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        replyService.update(writerId, replyId, multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> delete(@ExtractPayload Long writerId, @PathVariable Long replyId) {
        replyService.delete(writerId, replyId);
        return ResponseEntity.ok().build();
    }
}
