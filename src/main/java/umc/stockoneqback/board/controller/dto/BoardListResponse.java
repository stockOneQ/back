package umc.stockoneqback.board.controller.dto;

import umc.stockoneqback.board.infra.query.dto.BoardList;

import java.util.List;

public record BoardListResponse(
        List<BoardList> boardListResponses
){
}
