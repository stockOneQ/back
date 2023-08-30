package umc.stockoneqback.product.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ProductFindPage {
    private final Long id;
    private final String name;
    private final String imageUrl;
    private final Long stockQuant;

    @QueryProjection
    public ProductFindPage(Long id, String name, String imageUrl, Long stockQuant) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.stockQuant = stockQuant;
    }
}
