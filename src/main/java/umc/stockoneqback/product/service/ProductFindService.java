package umc.stockoneqback.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.product.domain.*;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductResponse;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.product.infra.query.dto.ProductFindPage;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.StoreService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductFindService {
    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final ProductService productService;
    private static final Integer PAGE_SIZE = 12;

    @Transactional
    public List<SearchProductResponse> searchProduct
            (Long userId, Long storeId, String storeConditionValue, String productName) throws IOException {
        Store store = storeService.findById(storeId);
        productService.checkRequestIdHasRequestStore(userId, store);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<ProductFindPage> searchProductUrlList = findProductAllByName(store, storeCondition, productName);
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProduct(Long userId, Long storeId, String storeConditionValue) {
        Store store = storeService.findById(storeId);
        productService.checkRequestIdHasRequestStore(userId, store);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        return countProduct(store, storeCondition);
    }

    @Transactional
    public List<SearchProductResponse> getListOfSearchProduct
            (Long userId, Long storeId, String storeConditionValue, String searchConditionValue, Long productId, String sortConditionValue) throws IOException {
        Product product = configPaging(productId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortConditionValue);
        Store store = storeService.findById(storeId);
        productService.checkRequestIdHasRequestStore(userId, store);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        SearchCondition searchCondition = SearchCondition.findSearchConditionByValue(searchConditionValue);
        List<ProductFindPage> searchProductUrlList = productRepository.findPageOfSearchConditionOrderBySortCondition
                (store, storeCondition, searchCondition, sortCondition, product.getName(), product.getOrderFreq(), PAGE_SIZE);
        return convertUrlToResponse(searchProductUrlList);
    }

    Product configPaging(Long productId) {
        if (productId == -1)
            return new Product();
        return productService.findProductById(productId);
    }

    List<ProductFindPage> findProductAllByName(Store store, StoreCondition storeCondition, String productName) {
        List<ProductFindPage> searchProductUrlList = productRepository.findProductByName(store, storeCondition, productName);
        if (searchProductUrlList.isEmpty())
            throw BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT);
        return searchProductUrlList;
    }

    List<GetTotalProductResponse> countProduct(Store store, StoreCondition storeCondition) {
        List<GetTotalProductResponse> countList = new ArrayList<>(4);
        LocalDate currentDate = LocalDate.now();
        LocalDate standardDate = currentDate.plusDays(3);
        countList.add(new GetTotalProductResponse
                ("Total", productRepository.countProductAll(store, storeCondition.getValue())));
        countList.add(new GetTotalProductResponse
                ("Pass", productRepository.countProductPass(store, storeCondition.getValue(), currentDate)));
        countList.add(new GetTotalProductResponse
                ("Close", productRepository.countProductClose(store, storeCondition.getValue(), currentDate, standardDate)));
        countList.add(new GetTotalProductResponse
                ("Lack", productRepository.countProductLack(store, storeCondition.getValue())));
        return countList;
    }

    private List<SearchProductResponse> convertUrlToResponse(List<ProductFindPage> searchProductUrlList) throws IOException {
        List<SearchProductResponse> searchProductResponseList = new ArrayList<>();
        for (ProductFindPage searchProductUrl : searchProductUrlList) {
            byte[] image = productService.getImageOrElseNull(searchProductUrl.getImageUrl());
            SearchProductResponse searchProductResponse = SearchProductResponse.builder()
                    .id(searchProductUrl.getId())
                    .name(searchProductUrl.getName())
                    .image(image)
                    .build();
            searchProductResponseList.add(searchProductResponse);
        }
        return searchProductResponseList;
    }
}
