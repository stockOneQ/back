package umc.stockoneqback.user.service.dto.response;

import lombok.Builder;

@Builder
public record GetFAResponse(
        String question,
        String answer
) {
}
