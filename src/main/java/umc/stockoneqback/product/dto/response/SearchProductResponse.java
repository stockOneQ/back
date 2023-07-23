package umc.stockoneqback.product.dto.response;

import lombok.Builder;

@Builder
public record SearchProductResponse(
        String name,

        byte[] image
) {
}

