package umc.stockoneqback.product.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LoadProductResponse(
        Long id,
        String name,
        Long price,
        String vendor,
        byte[] image,
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate receivingDate,
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate expirationDate,
        String location,
        Long requireQuantity,
        Long stockQuantity,
        String siteToOrder,
        Long orderFreq
) {
}
