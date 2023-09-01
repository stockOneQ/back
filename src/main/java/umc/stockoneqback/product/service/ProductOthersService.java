package umc.stockoneqback.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.product.domain.*;
import umc.stockoneqback.product.infra.query.dto.ProductFindPage;
import umc.stockoneqback.product.service.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.dto.response.SearchProductOthersResponse;
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
    public List<SearchProductOthersResponse> searchProductOthers(Store store, String storeConditionValue,
                                                                 String productName) throws IOException {
        StoreCondition storeCondition = StoreCondition.from(storeConditionValue);
        List<ProductFindPage> searchProductUrlList = productService.findProductAllByName(store, storeCondition, productName);
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProductOthers(Store store, String storeConditionValue) {
        StoreCondition storeCondition = StoreCondition.from(storeConditionValue);
        return productService.countProduct(store, storeCondition);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfSearchProductOthers(Store store, String storeConditionValue,
                                                                          String searchConditionValue, Long productId) throws IOException {
        Product product = productService.configPaging(productId);
        StoreCondition storeCondition = StoreCondition.from(storeConditionValue);
        SearchCondition searchCondition = SearchCondition.findSearchConditionByValue(searchConditionValue);
        List<ProductFindPage> searchProductUrlList = productRepository.findPageOfSearchConditionOrderBySortCondition
                (store, storeCondition, searchCondition, ProductSortCondition.NAME, product.getName(), product.getOrderFreq(), PAGE_SIZE);
        return convertUrlToResponse(searchProductUrlList);
    }

    private List<SearchProductOthersResponse> convertUrlToResponse(List<ProductFindPage> searchProductUrlList) throws IOException {
        List<SearchProductOthersResponse> searchProductOthersResponseList = new ArrayList<>();
        for (ProductFindPage productFindPage : searchProductUrlList) {
            byte[] image = productService.getImageOrElseNull(productFindPage.getImageUrl());
            SearchProductOthersResponse searchProductOthersResponse = SearchProductOthersResponse.builder()
                    .id(productFindPage.getId())
                    .name(productFindPage.getName())
                    .stockQuantity(productFindPage.getStockQuant())
                    .image(image)
                    .build();
            searchProductOthersResponseList.add(searchProductOthersResponse);
        }
        return searchProductOthersResponseList;
    }
}
