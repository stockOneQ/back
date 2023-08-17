package umc.stockoneqback.product.infra.query;

import umc.stockoneqback.product.domain.SearchCondition;
import umc.stockoneqback.product.domain.SortCondition;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.infra.query.dto.FindProductPage;
import umc.stockoneqback.role.domain.store.Store;

import java.util.List;

public interface FindProductQueryRepository {
    List<FindProductPage> findProductByName(Store store, StoreCondition storeCondition, String productName);

    List<FindProductPage> findPageOfSearchConditionOrderBySortCondition
            (Store store, StoreCondition storeCondition, SearchCondition searchCondition,
             SortCondition sortCondition, String productName, Long orderFreq, Integer pageSize);
}
