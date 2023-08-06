package umc.stockoneqback.board.controller.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BoardResponse(
        Long id,

        String title,

        byte[] file,

        String content,

        int hit,

        int likes,

        LocalDateTime createdDate,

        String writer
) {
}