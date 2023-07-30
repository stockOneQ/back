package umc.stockoneqback.friend.infra.query;

import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.user.infra.query.dto.FindManager;

import java.util.List;

public interface FriendFindQueryRepository {
    public List<FindManager> findReceiversByUserId(Long userId, FriendStatus friendStatus);
}
