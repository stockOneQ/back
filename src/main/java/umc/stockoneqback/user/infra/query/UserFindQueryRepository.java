package umc.stockoneqback.user.infra.query;

import umc.stockoneqback.user.domain.search.SearchType;
import umc.stockoneqback.user.infra.query.dto.FindManager;

import java.util.List;

public interface UserFindQueryRepository {
    List<FindManager> findManagersBySearchType(Long userId, SearchType searchType, String searchWord);
}
