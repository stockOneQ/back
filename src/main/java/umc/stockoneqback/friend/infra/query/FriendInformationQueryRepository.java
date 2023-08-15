package umc.stockoneqback.friend.infra.query;

import umc.stockoneqback.friend.infra.query.dto.response.FriendInformation;
import umc.stockoneqback.global.base.RelationStatus;

import java.util.List;

public interface FriendInformationQueryRepository {
    List<FriendInformation> findReceiversByUserIdAndRelationStatus(Long userId, RelationStatus relationStatus);
    List<FriendInformation> findSendersByUserIdAndRelationStatus(Long userId, RelationStatus relationStatus);
}
