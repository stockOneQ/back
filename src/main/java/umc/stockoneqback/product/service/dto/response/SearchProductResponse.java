package umc.stockoneqback.product.service.dto.response;

import lombok.Builder;

@Builder
public record SearchProductResponse(
        Long id,
        String name,
        byte[] image
) {
}

