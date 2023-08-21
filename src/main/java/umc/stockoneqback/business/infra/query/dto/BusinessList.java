package umc.stockoneqback.business.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.base.RelationStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BusinessList {
    private final Long id;
    private final String name;
    private final String storeName;
    private final String phoneNumber;
    private final String relationStatus;
    private final LocalDateTime lastModifiedDate;

    @QueryProjection
    public BusinessList(Long id, String name, String storeName, String phoneNumber, RelationStatus relationStatus, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.name = name;
        this.storeName = storeName;
        this.phoneNumber = phoneNumber;
        this.relationStatus = relationStatus.getValue();
        this.lastModifiedDate = lastModifiedDate;
    }
}
