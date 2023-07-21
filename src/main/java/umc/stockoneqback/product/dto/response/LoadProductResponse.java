package umc.stockoneqback.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LoadProductResponse(
        String name,

        Long price,

        String vendor,

        byte[] image,

        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate receivingDate,

        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate expirationDate,

        String location,

        Long requireQuant,

        Long stockQuant,

        String siteToOrder,

        Long orderFreq
) {
}