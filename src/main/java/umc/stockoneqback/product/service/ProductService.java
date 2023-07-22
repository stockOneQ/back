package umc.stockoneqback.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.product.domain.SortCondition;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.LoadProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductResponse;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.StoreService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final FileService fileService;
    private static final Integer PAGE_SIZE = 12;

    @Transactional
    public void saveProduct(Long storeId, String storeConditionValue, Product product, MultipartFile image) {
        Store store = storeService.findById(storeId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        isExistProductByName(store, storeCondition, product.getName());
        String imageUrl = null;
        if (image != null)
            imageUrl = fileService.uploadProductFiles(image);
        product.saveStoreAndStoreConditionAndImageUrl(storeCondition, store, imageUrl);
        productRepository.save(product);
    }

    @Transactional
    public LoadProductResponse loadProduct(Long productId) throws IOException {
        Product product = findProductById(productId);
        byte[] image = getImageOrElseNull(product.getImageUrl());
        return LoadProductResponse.builder()
                .id(product.getId())
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

    @Transactional
    public List<SearchProductResponse> searchProduct
            (Long storeId, String storeConditionValue, String productName) throws IOException {
        Store store = storeService.findById(storeId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<Product> searchProductUrlList = findProductAllByName(store, storeCondition, productName);
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public void editProduct(Long productId, Product newProduct, MultipartFile image) {
        Product product = findProductById(productId);
        String imageUrl = null;
        if (!image.isEmpty())
            imageUrl = fileService.uploadProductFiles(image);
        product.update(newProduct, imageUrl);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProductById(productId);
        product.delete();
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProduct(Long storeId, String storeConditionValue) {
        Store store = storeService.findById(storeId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        return countProduct(store, storeCondition);
    }

    @Transactional
    public List<SearchProductResponse> getListOfAllProduct
            (Long storeId, String storeConditionValue, Long productId, String sortBy) throws IOException {
        Product product = configPaging(productId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        Store store = storeService.findById(storeId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<Product> searchProductUrlList = new ArrayList<>();
        switch (sortCondition) {
            case NAME -> {
                searchProductUrlList = productRepository.findPageOfAllOrderByName
                        (store, storeCondition.getValue(), product.getName(), PAGE_SIZE);
            }
            case ORDER_FREQUENCY -> {
                searchProductUrlList = productRepository.findPageOfAllOrderByOrderFreq
                        (store, storeCondition.getValue(), product.getName(), product.getOrderFreq(), PAGE_SIZE);
            }
        }
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<SearchProductResponse> getListOfPassProduct
            (Long storeId, String storeConditionValue, Long productId, String sortBy) throws IOException {
        Product product = configPaging(productId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        Store store = storeService.findById(storeId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        LocalDate currentDate = LocalDate.now();
        List<Product> searchProductUrlList = new ArrayList<>();
        switch (sortCondition) {
            case NAME -> {
                searchProductUrlList = productRepository.findPageOfPassOrderByName
                        (store, storeCondition.getValue(), product.getName(), PAGE_SIZE, currentDate);
            }
            case ORDER_FREQUENCY -> {
                searchProductUrlList = productRepository.findPageOfPassOrderByOrderFreq
                        (store, storeCondition.getValue(), product.getName(), product.getOrderFreq(), PAGE_SIZE, currentDate);
            }
        }
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<SearchProductResponse> getListOfCloseProduct
            (Long storeId, String storeConditionValue, Long productId, String sortBy) throws IOException {
        Product product = configPaging(productId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        Store store = storeService.findById(storeId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        LocalDate currentDate = LocalDate.now();
        LocalDate standardDate = currentDate.plusDays(3);
        List<Product> searchProductUrlList = new ArrayList<>();
        switch (sortCondition) {
            case NAME -> {
                searchProductUrlList = productRepository.findPageOfCloseOrderByName
                        (store, storeCondition.getValue(), product.getName(), PAGE_SIZE, currentDate, standardDate);
            }
            case ORDER_FREQUENCY -> {
                searchProductUrlList = productRepository.findPageOfCloseOrderByOrderFreq
                        (store, storeCondition.getValue(), product.getName(), product.getOrderFreq(), PAGE_SIZE, currentDate, standardDate);
            }
        }
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public List<SearchProductResponse> getListOfLackProduct
            (Long storeId, String storeConditionValue, Long productId, String sortBy) throws IOException {
        Product product = configPaging(productId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        Store store = storeService.findById(storeId);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<Product> searchProductUrlList = new ArrayList<>();
        switch (sortCondition) {
            case NAME -> {
                searchProductUrlList = productRepository.findPageOfLackOrderByName
                        (store, storeCondition.getValue(), product.getName(), PAGE_SIZE);
            }
            case ORDER_FREQUENCY -> {
                searchProductUrlList = productRepository.findPageOfLackOrderByOrderFreq
                        (store, storeCondition.getValue(), product.getName(), product.getOrderFreq(), PAGE_SIZE);
            }
        }
        return convertUrlToResponse(searchProductUrlList);
    }

    private void isExistProductByName(Store store, StoreCondition storeCondition, String name) {
        Optional<Product> isExist = productRepository.isExistProductByName(store, storeCondition.getValue(), name);
        if (isExist.isPresent())
            throw BaseException.type(ProductErrorCode.DUPLICATE_PRODUCT);
    }

    private List<Product> findProductAllByName(Store store, StoreCondition storeCondition, String productName) {
        List<Product> searchProductUrlList = productRepository.findProductByName(store, storeCondition.getValue(), productName);
        if (searchProductUrlList.isEmpty())
            throw BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT);
        return searchProductUrlList;
    }

    private Product findProductById(Long productId) {
        return productRepository.findProductById(productId)
                .orElseThrow(() -> BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT));
    }

    private List<GetTotalProductResponse> countProduct(Store store, StoreCondition storeCondition) {
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

    private Product configPaging(Long productId) {
        if (productId == null)
            return new Product();
        return findProductById(productId);
    }

    private List<SearchProductResponse> convertUrlToResponse(List<Product> searchProductUrlList) throws IOException {
        List<SearchProductResponse> searchProductResponseList = new ArrayList<>();
        for (Product searchProductUrl : searchProductUrlList) {
            byte[] image = getImageOrElseNull(searchProductUrl.getImageUrl());
            SearchProductResponse searchProductResponse = SearchProductResponse.builder()
                    .id(searchProductUrl.getId())
                    .name(searchProductUrl.getName())
                    .image(image)
                    .build();
            searchProductResponseList.add(searchProductResponse);
        }
        return searchProductResponseList;
    }

    private byte[] getImageOrElseNull(String imageUrl) throws IOException {
        if (imageUrl == null)
            return null;
        return fileService.downloadToResponseDto(imageUrl);
    }
}
