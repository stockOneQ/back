package umc.stockoneqback.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.board.controller.dto.CustomBoardListResponse;
import umc.stockoneqback.board.infra.query.dto.BoardList;
import umc.stockoneqback.board.service.BoardListService;
import umc.stockoneqback.global.annotation.ExtractPayload;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardListApiController {
    private final BoardListService boardListService;

    @GetMapping("")
    public ResponseEntity<CustomBoardListResponse<BoardList>> boardList(
                                            @ExtractPayload Long userId,
                                            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                            @RequestParam(value = "sort", required = false, defaultValue = "최신순") String sortBy,
                                            @RequestParam(value = "search", required = false, defaultValue = "제목") String searchBy,
                                            @RequestParam(value = "word", required = false, defaultValue = "") String searchWord) throws IOException {
        return ResponseEntity.ok(boardListService.getBoardList(userId, page, sortBy, searchBy, searchWord));
    }

    @GetMapping("/my")
    public ResponseEntity<CustomBoardListResponse<BoardList>> myBoardList(
                                            @ExtractPayload Long userId,
                                            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                            @RequestParam(value = "sort", required = false, defaultValue = "최신순") String sortBy,
                                            @RequestParam(value = "search", required = false, defaultValue = "제목") String searchBy,
                                            @RequestParam(value = "word", required = false, defaultValue = "") String searchWord) throws IOException {
        return ResponseEntity.ok(boardListService.getMyBoardList(userId, page, sortBy, searchBy, searchWord));
    }

    @DeleteMapping("/my")
    public ResponseEntity<Void> deleteMyBoard(@ExtractPayload Long userId,
                                              @RequestParam List<Long> boardId) throws IOException {
        boardListService.deleteMyBoard(userId, boardId);
        return ResponseEntity.ok().build();
    }
}
