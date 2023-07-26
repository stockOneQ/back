package umc.stockoneqback.reply.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.reply.controller.dto.ReplyRequest;
import umc.stockoneqback.reply.service.ReplyService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyApiController {
    private final ReplyService replyService;

    @PostMapping("/{commentId}")
    public ResponseEntity<Void> create(@ExtractPayload Long writerId, @PathVariable Long commentId,
                                       @RequestBody @Valid ReplyRequest request) {
        replyService.create(writerId, commentId, request.image(), request.content());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<Void> update(@ExtractPayload Long writerId, @PathVariable Long replyId,
                                       @RequestBody @Valid ReplyRequest request) {
        replyService.update(writerId, replyId, request.image(), request.content());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> delete(@ExtractPayload Long writerId, @PathVariable Long replyId) {
        replyService.delete(writerId, replyId);
        return ResponseEntity.ok().build();
    }
}
