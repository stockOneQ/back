package umc.stockoneqback.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.board.controller.dto.BoardListResponse;
import umc.stockoneqback.board.service.BoardListService;
import umc.stockoneqback.global.annotation.ExtractPayload;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardListApiController {
    private final BoardListService boardListService;

    @GetMapping
    public ResponseEntity<BoardListResponse> boardList(@ExtractPayload Long userId,
                                                       @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastBoardId,
                                                       @RequestParam(value = "sort", required = false, defaultValue = "최신순") String sortBy,
                                                       @RequestParam(value = "search", required = false, defaultValue = "제목") String searchBy,
                                                       @RequestParam(value = "word", required = false, defaultValue = "") String searchWord) throws IOException {
        return ResponseEntity.ok(boardListService.getBoardList(userId, lastBoardId, sortBy, searchBy, searchWord));
    }

    @GetMapping("/my")
    public ResponseEntity<BoardListResponse> myBoardList(@ExtractPayload Long userId,
                                                       @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastBoardId,
                                                       @RequestParam(value = "sort", required = false, defaultValue = "최신순") String sortBy,
                                                       @RequestParam(value = "search", required = false, defaultValue = "제목") String searchBy,
                                                       @RequestParam(value = "word", required = false, defaultValue = "") String searchWord) throws IOException {
        return ResponseEntity.ok(boardListService.getMyBoardList(userId, lastBoardId, sortBy, searchBy, searchWord));
    }

    @DeleteMapping("/my")
    public ResponseEntity<Void> deleteMyBoard(@ExtractPayload Long userId,
                                              @RequestParam List<Long> selectedBoardId) throws IOException {
        boardListService.deleteMyBoard(userId, selectedBoardId);
        return ResponseEntity.ok().build();
    }
}
