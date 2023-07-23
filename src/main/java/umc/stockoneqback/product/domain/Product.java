package umc.stockoneqback.product.domain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.role.domain.store.Store;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long price;

    private String vendor;

    private String imageUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receivingDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @Nullable
    private String location;

    private Long requireQuant;

    private Long stockQuant;

    @Nullable
    private String siteToOrder;

    private Long orderFreq;

    @Convert(converter = StoreCondition.StoreConditionConverter.class)
    private StoreCondition storeCondition;

    @Convert(converter = Status.StatusConverter.class)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Builder
    public Product(String name, Long price, String vendor, LocalDate receivingDate, LocalDate expirationDate,
                   String location, Long requireQuant, Long stockQuant, String siteToOrder, Long orderFreq) {
        this.name = name;
        this.price = price;
        this.vendor = vendor;
        this.receivingDate = receivingDate;
        this.expirationDate = expirationDate;
        this.location = location;
        this.requireQuant = requireQuant;
        this.stockQuant = stockQuant;
        this.siteToOrder = siteToOrder;
        this.orderFreq = orderFreq;
        this.status = Status.NORMAL;
    }

    public void saveStoreAndStoreConditionAndImageUrl(StoreCondition storeCondition, Store store, String imageUrl) {
        this.storeCondition = storeCondition;
        this.store = store;
        this.imageUrl = imageUrl;
    }

    public void update(Product newProduct, String imageUrl) {
        this.name = newProduct.getName();
        this.price = newProduct.getPrice();
        this.vendor = newProduct.getVendor();
        this.receivingDate = newProduct.getReceivingDate();
        this.expirationDate = newProduct.getExpirationDate();
        this.location = newProduct.getLocation();
        this.requireQuant = newProduct.getRequireQuant();
        this.stockQuant = newProduct.getStockQuant();
        this.siteToOrder = newProduct.getSiteToOrder();
        this.orderFreq = newProduct.getOrderFreq();
        this.imageUrl = imageUrl;
    }

    public void delete() {
        this.status = Status.EXPIRED;
    }
}
