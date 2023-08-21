package umc.stockoneqback.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.product.domain.*;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.product.infra.query.dto.FindProductPage;
import umc.stockoneqback.role.domain.store.Store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductOthersService {
    private final ProductRepository productRepository;
    private final ProductService productService;
    private static final Integer PAGE_SIZE = 9;

    @Transactional
    public List<SearchProductOthersResponse> searchProductOthers
            (Store store, String storeConditionValue, String productName) throws IOException {
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<FindProductPage> searchProductUrlList = productService.findProductAllByName(store, storeCondition, productName);
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProductOthers(Store store, String storeConditionValue) {
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        return productService.countProduct(store, storeCondition);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfSearchProductOthers
            (Store store, String storeConditionValue, String searchConditionValue, Long productId) throws IOException {
        Product product = productService.configPaging(productId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        SearchCondition searchCondition = SearchCondition.findSearchConditionByValue(searchConditionValue);
        List<FindProductPage> searchProductUrlList = productRepository.findPageOfSearchConditionOrderBySortCondition
                (store, storeCondition, searchCondition, SortCondition.NAME, product.getName(), product.getOrderFreq(), PAGE_SIZE);
        return convertUrlToResponse(searchProductUrlList);
    }

    private List<SearchProductOthersResponse> convertUrlToResponse(List<FindProductPage> searchProductUrlList) throws IOException {
        List<SearchProductOthersResponse> searchProductOthersResponseList = new ArrayList<>();
        for (FindProductPage findProductPage : searchProductUrlList) {
            byte[] image = productService.getImageOrElseNull(findProductPage.getImageUrl());
            SearchProductOthersResponse searchProductOthersResponse = SearchProductOthersResponse.builder()
                    .id(findProductPage.getId())
                    .name(findProductPage.getName())
                    .stockQuant(findProductPage.getStockQuant())
                    .image(image)
                    .build();
            searchProductOthersResponseList.add(searchProductOthersResponse);
        }
        return searchProductOthersResponseList;
    }
}
