package umc.stockoneqback.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.board.controller.dto.BoardListResponse;
import umc.stockoneqback.board.service.BoardListService;
import umc.stockoneqback.global.annotation.ExtractPayload;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardListApiController {
    private final BoardListService boardListService;

    @GetMapping
    public ResponseEntity<BoardListResponse> boardList(@ExtractPayload Long userId,
                                                       @RequestParam(value = "last", required = false, defaultValue = "-1") Long boardId,
                                                       @RequestParam(value = "sort", defaultValue = "최신순") String sortBy) throws IOException {
        return ResponseEntity.ok(boardListService.getBoardList(userId, boardId, sortBy));
    }
}
