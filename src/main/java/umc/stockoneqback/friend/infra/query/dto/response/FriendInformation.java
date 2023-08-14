package umc.stockoneqback.friend.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.base.RelationStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FriendInformation {
    private final Long id;
    private final String name;
    private final String storeName;
    private final String phoneNumber;
    private final String relationStatus;
    private final LocalDateTime lastModifiedDate;

    @QueryProjection
    public FriendInformation(Long id, String name, String storeName, String phoneNumber, RelationStatus relationStatus, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.name = name;
        this.storeName = storeName;
        this.phoneNumber = phoneNumber;
        this.relationStatus = relationStatus.getValue();
        this.lastModifiedDate = lastModifiedDate;
    }
}
