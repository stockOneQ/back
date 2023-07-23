package umc.stockoneqback.product.dto.request;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.product.domain.Product;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record EditProductRequest(
        @NotBlank(message = "제품명은 필수입니다.")
        String name,

        @NotBlank(message = "가격은 필수입니다.")
        Long price,

        @NotBlank(message = "판매업체는 필수입니다.")
        String vendor,

        @NotBlank(message = "제품 이미지는 필수입니다.")
        MultipartFile image,

        @NotNull(message = "입고일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate receivingDate,

        @NotNull(message = "유통기한은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate expirationDate,

        String location,

        @NotBlank(message = "필수 수량은 필수입니다.")
        Long requireQuant,

        @NotBlank(message = "재고 수량은 필수입니다.")
        Long stockQuant,

        String siteToOrder,

        @NotBlank(message = "발주 빈도는 필수입니다.")
        Long orderFreq
) {
        public Product toProduct() {
                return Product.builder()
                        .name(name)
                        .price(price)
                        .vendor(vendor)
                        .receivingDate(receivingDate)
                        .expirationDate(expirationDate)
                        .location(location)
                        .requireQuant(requireQuant)
                        .stockQuant(stockQuant)
                        .siteToOrder(siteToOrder)
                        .orderFreq(orderFreq)
                        .build();
        }
}
