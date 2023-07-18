package umc.stockoneqback.product.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.role.domain.store.Store;

import javax.persistence.*;
import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
/*
 * TODO : image type change required
 * */
@Table(name = "product")
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long price;

    private String vendor;

    private String image;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date receivingDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirationDate;

    @Nullable
    private String location;

    private Long requireQuant;

    private Long stockQuant;

    @Nullable
    private String siteToOrder;

    private Long orderFreq;

    private StoreCondition storeCondition;

    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
}
