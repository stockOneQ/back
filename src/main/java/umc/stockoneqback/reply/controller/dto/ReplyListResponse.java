package umc.stockoneqback.reply.controller.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReplyListResponse(
        Long id,
        byte[] image,
        String content,
        LocalDateTime createdDate,
        String writerId,
        String writerName
) {
}
