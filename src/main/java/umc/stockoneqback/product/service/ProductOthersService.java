package umc.stockoneqback.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.role.domain.store.Store;

import java.io.IOException;
import java.time.LocalDate;
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
        List<Product> searchProductUrlList = productService.findProductAllByName(store, storeCondition, productName);
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProductOthers(Store store, String storeConditionValue) {
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        return productService.countProduct(store, storeCondition);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfAllProductOthers
            (Store store, String storeConditionValue, Long productId) throws IOException {
        Product product = productService.configPaging(productId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<Product> searchProductUrlList = productRepository.findPageOfAllOrderByName
                (store, storeCondition.getValue(), product.getName(), PAGE_SIZE);
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfPassProductOthers
            (Store store, String storeConditionValue, Long productId) throws IOException {
        Product product = productService.configPaging(productId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        LocalDate currentDate = LocalDate.now();
        List<Product> searchProductUrlList = productRepository.findPageOfPassOrderByName
                (store, storeCondition.getValue(), product.getName(), PAGE_SIZE, currentDate);
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfCloseProductOthers
            (Store store, String storeConditionValue, Long productId) throws IOException {
        Product product = productService.configPaging(productId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        LocalDate currentDate = LocalDate.now();
        LocalDate standardDate = currentDate.plusDays(3);
        List<Product> searchProductUrlList = productRepository.findPageOfCloseOrderByName
                (store, storeCondition.getValue(), product.getName(), PAGE_SIZE, currentDate, standardDate);
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfLackProductOthers
            (Store store, String storeConditionValue, Long productId) throws IOException {
        Product product = productService.configPaging(productId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<Product> searchProductUrlList = productRepository.findPageOfLackOrderByName
                (store, storeCondition.getValue(), product.getName(), PAGE_SIZE);
        return convertUrlToResponse(searchProductUrlList);
    }

    private List<SearchProductOthersResponse> convertUrlToResponse(List<Product> searchProductUrlList) throws IOException {
        List<SearchProductOthersResponse> searchProductOthersResponseList = new ArrayList<>();
        for (Product searchProductUrl : searchProductUrlList) {
            byte[] image = productService.getImageOrElseNull(searchProductUrl.getImageUrl());
            SearchProductOthersResponse searchProductOthersResponse = SearchProductOthersResponse.builder()
                    .id(searchProductUrl.getId())
                    .name(searchProductUrl.getName())
                    .stockQuant(searchProductUrl.getStockQuant())
                    .image(image)
                    .build();
            searchProductOthersResponseList.add(searchProductOthersResponse);
        }
        return searchProductOthersResponseList;
    }
}
