package umc.stockoneqback.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.board.controller.dto.BoardListResponse;
import umc.stockoneqback.board.service.BoardListService;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardListApiController {
    private final BoardListService boardListService;

    @GetMapping
    public BaseResponse<List<BoardListResponse>> boardList(@ExtractPayload Long userId,
                                                           @RequestParam(value = "last", required = false) Long boardId,
                                                           @RequestParam(value = "sort") String sortBy) throws IOException {
        return new BaseResponse<>(boardListService.getBoardList(userId, boardId, sortBy));
    }
}
