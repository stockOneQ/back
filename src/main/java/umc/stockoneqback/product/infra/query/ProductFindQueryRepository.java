package umc.stockoneqback.product.infra.query;

import umc.stockoneqback.product.domain.ProductSortCondition;
import umc.stockoneqback.product.domain.SearchCondition;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.infra.query.dto.ProductFindPage;
import umc.stockoneqback.role.domain.store.Store;

import java.util.List;

public interface ProductFindQueryRepository {
    List<ProductFindPage> findProductByName(Store store, StoreCondition storeCondition, String productName);
    List<ProductFindPage> findPageOfSearchConditionOrderBySortCondition(Store store, StoreCondition storeCondition,
                                                                        SearchCondition searchCondition,
                                                                        ProductSortCondition productSortCondition,
                                                                        String productName, Long orderFreq, Integer pageSize);
}
