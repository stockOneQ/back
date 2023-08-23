package umc.stockoneqback.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.product.domain.*;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.product.infra.query.dto.ProductFindPage;
import umc.stockoneqback.role.domain.store.Store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductFindOthersService {
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ProductFindService productFindService;
    private static final Integer PAGE_SIZE = 9;

    @Transactional
    public List<SearchProductOthersResponse> searchProductOthers
            (Store store, String storeConditionValue, String productName) throws IOException {
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<ProductFindPage> searchProductUrlList = productFindService.findProductAllByName(store, storeCondition, productName);
        return convertUrlToResponseOthers(searchProductUrlList);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProductOthers(Store store, String storeConditionValue) {
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        return productFindService.countProduct(store, storeCondition);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfSearchProductOthers
            (Store store, String storeConditionValue, String searchConditionValue, Long productId) throws IOException {
        Product product = productFindService.configPaging(productId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        SearchCondition searchCondition = SearchCondition.findSearchConditionByValue(searchConditionValue);
        List<ProductFindPage> searchProductUrlList = productRepository.findPageOfSearchConditionOrderBySortCondition
                (store, storeCondition, searchCondition, SortCondition.NAME, product.getName(), product.getOrderFreq(), PAGE_SIZE);
        return convertUrlToResponseOthers(searchProductUrlList);
    }

    private List<SearchProductOthersResponse> convertUrlToResponseOthers(List<ProductFindPage> searchProductUrlList) throws IOException {
        List<SearchProductOthersResponse> searchProductOthersResponseList = new ArrayList<>();
        for (ProductFindPage productFindPage : searchProductUrlList) {
            byte[] image = productService.getImageOrElseNull(productFindPage.getImageUrl());
            SearchProductOthersResponse searchProductOthersResponse = SearchProductOthersResponse.builder()
                    .id(productFindPage.getId())
                    .name(productFindPage.getName())
                    .stockQuant(productFindPage.getStockQuant())
                    .image(image)
                    .build();
            searchProductOthersResponseList.add(searchProductOthersResponse);
        }
        return searchProductOthersResponseList;
    }
}
