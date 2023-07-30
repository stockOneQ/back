package umc.stockoneqback.friend.infra.query;

import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.user.service.dto.FindManager;

import java.util.List;

public interface FriendFindQueryRepository {
    public List<FindManager> findReceiversByUserIdAndName(Long userId, String name, FriendStatus friendStatus);
}
