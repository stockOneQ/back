package umc.stockoneqback.board.controller.dto;

import lombok.Builder;

@Builder
public record BoardResponse(
        Long id,

        String title,

        byte[] file,

        String content
) {
}
