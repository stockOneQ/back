package umc.stockoneqback.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.board.controller.dto.BoardRequest;
import umc.stockoneqback.board.service.BoardService;
import umc.stockoneqback.global.annotation.ExtractPayload;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Void> create(@ExtractPayload Long writerId,
                                       @RequestBody @Valid BoardRequest request,
                                       @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        Long boardId = boardService.create(writerId, request.title(), multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<Void> update(@ExtractPayload Long writerId,
                                       @PathVariable Long boardId,
                                       @RequestBody @Valid BoardRequest request,
                                       @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        boardService.update(writerId, boardId, request.title(), multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@ExtractPayload Long writerId, @PathVariable Long boardId) {
        boardService.delete(writerId, boardId);
        return ResponseEntity.noContent().build();
    }
}
