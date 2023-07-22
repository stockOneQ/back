package umc.stockoneqback.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.dto.response.LoadProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductUrl;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.StoreService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final FileService fileService;

    @Transactional
    public void saveProduct(Long storeId, String storeConditionValue, Product product, MultipartFile image) {
        Store store = storeService.findById(storeId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        //isExistProductByName(store, storeCondition, product.getName());
        String imageUrl = fileService.uploadProductFiles(image);
        product.saveStoreAndStoreConditionAndImageUrl(storeCondition, store, imageUrl);
        productRepository.save(product);
    }

    @Transactional
    public LoadProductResponse loadProduct(Long productId) throws IOException {
        Product product = findProductById(productId);
        byte[] image = fileService.downloadToResponseDto(product.getImageUrl());
        return LoadProductResponse.builder()
                .name(product.getName())
                .price(product.getPrice())
                .vendor(product.getVendor())
                .image(image)
                .receivingDate(product.getReceivingDate())
                .expirationDate(product.getExpirationDate())
                .location(product.getLocation())
                .requireQuant(product.getRequireQuant())
                .stockQuant(product.getStockQuant())
                .siteToOrder(product.getSiteToOrder())
                .orderFreq(product.getOrderFreq())
                .build();
    }
/*
    @Transactional
    public List<SearchProductResponse> searchProduct(Long storeId, String storeConditionValue, String productName) throws IOException {
        Store store = storeService.findById(storeId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<SearchProductUrl> searchProductUrlList = findProductAllByName(store, storeCondition, productName);
        List<SearchProductResponse> searchProductResponseList = new ArrayList<>();
        for (SearchProductUrl searchProductUrl : searchProductUrlList) {
            byte[] image = fileService.downloadToResponseDto(searchProductUrl.imageUrl());
            SearchProductResponse searchProductResponse = SearchProductResponse.builder()
                    .name(searchProductUrl.name())
                    .image(image)
                    .build();
            searchProductResponseList.add(searchProductResponse);
        }
        return searchProductResponseList;
    }
*/
    @Transactional
    public void editProduct(Long productId, Product newProduct, MultipartFile image) {
        Product product = findProductById(productId);
        String imageUrl = fileService.uploadProductFiles(image);
        product.update(newProduct, imageUrl);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProductById(productId);
        product.delete();
    }
/*
    private void isExistProductByName(Store store, StoreCondition storeCondition, String name) {
        Integer isExist = productRepository.isExistProductByName(store, storeCondition, name);
        if (isExist != null)
            throw BaseException.type(ProductErrorCode.DUPLICATE_PRODUCT);
    }

    private List<SearchProductUrl> findProductAllByName(Store store, StoreCondition storeCondition, String productName) {
        List<SearchProductUrl> searchProductUrlList = productRepository.findProductByName(store, storeCondition, productName);
        if (searchProductUrlList.isEmpty())
            throw BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT);
        return searchProductUrlList;
    }*/

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT));
    }
}
