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
                                       @RequestPart(value = "request") @Valid BoardRequest request,
                                       @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        Long boardId = boardService.create(writerId, request.title(), multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<Void> update(@ExtractPayload Long writerId,
                                       @PathVariable Long boardId,
                                       @RequestPart(value = "request")  @Valid BoardRequest request,
                                       @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        boardService.update(writerId, boardId, request.title(), multipartFile, request.content());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@ExtractPayload Long writerId, @PathVariable Long boardId) {
        boardService.delete(writerId, boardId);
        return ResponseEntity.ok().build();
    }
}
