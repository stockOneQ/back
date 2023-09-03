package umc.stockoneqback.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.board.controller.dto.BoardRequest;
import umc.stockoneqback.board.controller.dto.BoardResponse;
import umc.stockoneqback.board.service.BoardService;
import umc.stockoneqback.global.annotation.ExtractPayload;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {
    private final BoardService boardService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("")
    public ResponseEntity<Void> create(@ExtractPayload Long writerId,
                                       @RequestBody @Valid BoardRequest request) {
        Long boardId = boardService.create(writerId, request.title(), request.content());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{boardId}")
    public ResponseEntity<Void> update(@ExtractPayload Long writerId, @PathVariable Long boardId,
                                       @RequestBody @Valid BoardRequest request) {
        boardService.update(writerId, boardId, request.title(), request.content());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> loadBoard(@ExtractPayload Long userId, @PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.loadBoard(userId, boardId));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{boardId}/hit")
    public ResponseEntity<Void> updateHit(@ExtractPayload Long userId, @PathVariable Long boardId) {
        boardService.updateHit(userId, boardId);
        return ResponseEntity.ok().build();
    }
}
