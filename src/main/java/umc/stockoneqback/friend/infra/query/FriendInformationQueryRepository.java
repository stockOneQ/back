package umc.stockoneqback.friend.infra.query;

import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.infra.query.dto.response.FriendInformation;

import java.util.List;

public interface FriendInformationQueryRepository {
    List<FriendInformation> findReceiversByUserIdAndFriendStatus(Long userId, FriendStatus friendStatus);
    List<FriendInformation> findSendersByUserIdAndFriendStatus(Long userId, FriendStatus friendStatus);
}
