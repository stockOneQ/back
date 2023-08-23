package umc.stockoneqback.business.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BusinessList {
    private final Long id;
    private final String name;
    private final String storeCoName;
    private final String phoneNumber;
    private final LocalDateTime lastModifiedDate;

    @QueryProjection
    public BusinessList(Long id, String name, String storeName, String phoneNumber, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.name = name;
        this.storeCoName = storeName;
        this.phoneNumber = phoneNumber;
        this.lastModifiedDate = lastModifiedDate;
    }
}
