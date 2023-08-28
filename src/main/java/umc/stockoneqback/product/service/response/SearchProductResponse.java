package umc.stockoneqback.product.service.response;

import lombok.Builder;

@Builder
public record SearchProductResponse(
        Long id,

        String name,

        byte[] image
) {
}

