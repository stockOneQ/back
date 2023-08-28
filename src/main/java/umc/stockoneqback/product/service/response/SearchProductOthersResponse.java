package umc.stockoneqback.product.service.response;

import lombok.Builder;

@Builder
public record SearchProductOthersResponse(
        Long id,
        String name,
        Long stockQuantity,
        byte[] image
) {
}
