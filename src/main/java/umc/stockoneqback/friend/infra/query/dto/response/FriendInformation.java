package umc.stockoneqback.friend.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.friend.domain.FriendStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FriendInformation {
    private final Long id;
    private final String name;
    private final String storeName;
    private final String phoneNumber;
    private final String friendStatus;
    private final LocalDateTime lastModifiedDate;

    @QueryProjection
    public FriendInformation(Long id, String name, String storeName, String phoneNumber, FriendStatus friendStatus, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.name = name;
        this.storeName = storeName;
        this.phoneNumber = phoneNumber;
        this.friendStatus = friendStatus.getValue();
        this.lastModifiedDate = lastModifiedDate;
    }
}
