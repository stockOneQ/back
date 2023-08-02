package umc.stockoneqback.board.controller.like;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.board.service.like.BoardLikeService;
import umc.stockoneqback.global.annotation.ExtractPayload;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/likes")
public class BoardLikeApiController {
    private final BoardLikeService boardLikeService;

    @PostMapping
    public ResponseEntity<Void> register(@ExtractPayload Long userId, @PathVariable Long boardId) {
        boardLikeService.register(userId, boardId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancel(@ExtractPayload Long userId, @PathVariable Long boardId) {
        boardLikeService.cancel(userId, boardId);
        return ResponseEntity.ok().build();
    }
}

