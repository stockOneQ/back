package umc.stockoneqback.share.infra.query;

import umc.stockoneqback.share.domain.Category;
import umc.stockoneqback.share.domain.SearchType;
import umc.stockoneqback.share.infra.query.dto.CustomShareListPage;
import umc.stockoneqback.share.infra.query.dto.ShareList;

public interface ShareListQueryRepository{
    CustomShareListPage<ShareList> findShareList(Long businessId, Category category, SearchType searchType, String searchWord, int page);
}
