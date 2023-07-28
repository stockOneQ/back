package umc.stockoneqback.board.controller.dto;

import lombok.Builder;

@Builder
public record BoardListResponse (
        Long id,
        String title,
        String content,
        int hit,
        int comment,
        int like
){
}
