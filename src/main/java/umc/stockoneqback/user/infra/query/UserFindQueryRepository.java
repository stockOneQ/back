package umc.stockoneqback.user.infra.query;

import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.user.service.dto.FindManager;

import java.util.List;

public interface UserFindQueryRepository {
    public List<FindManager> findUsersByName(String name);
}
