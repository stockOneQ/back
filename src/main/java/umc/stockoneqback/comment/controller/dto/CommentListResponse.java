package umc.stockoneqback.comment.controller.dto;

import lombok.Builder;
import umc.stockoneqback.reply.controller.dto.ReplyListResponse;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CommentListResponse(
        Long id,
        byte[] image,
        String content,
        LocalDateTime createdDate,
        String writerId,
        String writerName,
        List<ReplyListResponse> replyList
) {
}
