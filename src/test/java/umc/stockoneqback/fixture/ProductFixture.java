package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.role.domain.store.Store;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public enum ProductFixture {
    APPLE("사과", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(3), null, 3L, 3L, null,
            60L, StoreCondition.ROOM),
    BANANA("바나나", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(5), null, 3L, 5L, null,
                    100L, StoreCondition.ROOM),
    MANGO("망고", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(5), null, 3L, 5L, null,
            20L, StoreCondition.ROOM),
    DURIAN("두리안", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 20),
            LocalDate.now().plusDays(1), null, 3L, 5L, null,
            60L, StoreCondition.ROOM),
    ORANGE("오렌지", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(10), null, 3L, 5L, null,
            60L, StoreCondition.ROOM),
    PINEAPPLE("파인애플", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(10), null, 3L, 1L, null,
            80L, StoreCondition.ROOM),
    GRAPE("포도", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().minusDays(1), null, 3L, 4L, null,
            40L, StoreCondition.ROOM),
    CHERRY("체리", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(2), null, 10L, 5L, null,
            20L, StoreCondition.ROOM),
    MELON("메론", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now(), null, 5L, 7L, null,
            40L, StoreCondition.ROOM),
    WATERMELON("수박", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(7), null, 3L, 4L, null,
            100L, StoreCondition.ROOM),
    BLUEBERRY("블루베리", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(4), null, 2L, 3L, null,
            80L, StoreCondition.ROOM),
    STRAWBERRY("딸기", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(2), null, 3L, 5L, null,
            100L, StoreCondition.REFRIGERATING),
    PEACH("복숭아", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(6), null, 2L, 1L, null,
            20L, StoreCondition.ROOM),
    PLUM("자두", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(2), null, 1L, 2L, null,
            20L, StoreCondition.ROOM),
    TANGERINE("귤", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(9), null, 3L, 9L, null,
            40L, StoreCondition.ROOM),
    PERSIMMON("감", 1000L, "A 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().minusDays(2), null, 3L, 5L, null,
            60L, StoreCondition.ROOM),
    PEAR("배", 1000L, "B 납품업체", null, LocalDate.of(2023, 7, 22),
            LocalDate.now().plusDays(8), null, 3L, 3L, null,
            80L, StoreCondition.ROOM)
    ;
    private final String name;

    private final Long price;

    private final String vendor;

    private final String imageUrl;

    private final LocalDate receivingDate;

    private final LocalDate expirationDate;

    private final String location;

    private final Long requireQuant;

    private final Long stockQuant;

    private final String siteToOrder;

    private final Long orderFreq;

    private final StoreCondition storeCondition;

    public Product toProduct(Store store) {
        return Product.createProduct(name, price, vendor, receivingDate, expirationDate,
                location, requireQuant, stockQuant, siteToOrder, orderFreq, storeCondition, store, imageUrl);
    }
}
