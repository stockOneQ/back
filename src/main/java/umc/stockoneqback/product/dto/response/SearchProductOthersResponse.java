package umc.stockoneqback.product.dto.response;

import lombok.Builder;

@Builder
public record SearchProductOthersResponse(
        Long id,

        String name,

        Long stockQuant,

        byte[] image
) {
}
