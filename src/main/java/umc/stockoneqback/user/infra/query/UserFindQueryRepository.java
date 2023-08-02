package umc.stockoneqback.user.infra.query;

import umc.stockoneqback.user.infra.query.dto.FindManager;

import java.util.List;

public interface UserFindQueryRepository {
    public List<FindManager> findManagersByName(String name);
    public List<FindManager> findManagersByStoreName(String storeName);
    public List<FindManager> findManagersByAddress(String address);
    public List<FindManager> findUserByUserId(Long id);
}
