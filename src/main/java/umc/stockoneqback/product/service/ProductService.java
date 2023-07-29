package umc.stockoneqback.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.auth.domain.Token;
import umc.stockoneqback.auth.service.TokenService;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.GlobalErrorCode;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.product.domain.SortCondition;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.dto.response.GetListOfPassProductByOnlineUsersResponse;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.LoadProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductResponse;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.PartTimerService;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

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
    private final TokenService tokenService;
    private final UserFindService userFindService;
    private final PartTimerService partTimerService;
    private static final Integer PAGE_SIZE = 12;

    @Transactional
    public void saveProduct(Long userId, Long storeId, String storeConditionValue, Product product, MultipartFile image) {
        Store store = storeService.findById(storeId);
        checkRequestIdHasRequestStore(userId, store);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        isExistProductByName(store, storeCondition, product.getName());
        String imageUrl = null;
        if (image != null)
            imageUrl = fileService.uploadProductFiles(image);
        product.saveStoreAndStoreConditionAndImageUrl(storeCondition, store, imageUrl);
        productRepository.save(product);
    }

    @Transactional
    public LoadProductResponse loadProduct(Long userId, Long productId) throws IOException {
        Product product = findProductById(productId);
        checkRequestIdHasRequestProduct(userId, product);
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
            (Long userId, Long storeId, String storeConditionValue, String productName) throws IOException {
        Store store = storeService.findById(storeId);
        checkRequestIdHasRequestStore(userId, store);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        List<Product> searchProductUrlList = findProductAllByName(store, storeCondition, productName);
        return convertUrlToResponse(searchProductUrlList);
    }

    @Transactional
    public void editProduct(Long userId, Long productId, Product newProduct, MultipartFile image) {
        Product product = findProductById(productId);
        checkRequestIdHasRequestProduct(userId, product);
        String imageUrl = null;
        if (image != null)
            imageUrl = fileService.uploadProductFiles(image);
        product.update(newProduct, imageUrl);
    }

    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        Product product = findProductById(productId);
        checkRequestIdHasRequestProduct(userId, product);
        product.delete();
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProduct(Long userId, Long storeId, String storeConditionValue) {
        Store store = storeService.findById(storeId);
        checkRequestIdHasRequestStore(userId, store);
        StoreCondition storeCondition = StoreCondition.findStoreConditionByValue(storeConditionValue);
        return countProduct(store, storeCondition);
    }

    @Transactional
    public List<SearchProductResponse> getListOfAllProduct
            (Long userId, Long storeId, String storeConditionValue, Long productId, String sortBy) throws IOException {
        Product product = configPaging(productId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        Store store = storeService.findById(storeId);
        checkRequestIdHasRequestStore(userId, store);
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
            (Long userId, Long storeId, String storeConditionValue, Long productId, String sortBy) throws IOException {
        Product product = configPaging(productId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        Store store = storeService.findById(storeId);
        checkRequestIdHasRequestStore(userId, store);
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
            (Long userId, Long storeId, String storeConditionValue, Long productId, String sortBy) throws IOException {
        Product product = configPaging(productId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        Store store = storeService.findById(storeId);
        checkRequestIdHasRequestStore(userId, store);
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
            (Long userId, Long storeId, String storeConditionValue, Long productId, String sortBy) throws IOException {
        Product product = configPaging(productId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        Store store = storeService.findById(storeId);
        checkRequestIdHasRequestStore(userId, store);
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

    @Transactional
    public List<GetListOfPassProductByOnlineUsersResponse> getListOfPassProductByOnlineUsers() {
        List<Token> tokenList = tokenService.findAllOnlineUsers();
        List<GetListOfPassProductByOnlineUsersResponse> getListOfPassProductByOnlineUsersResponseList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (Token token: tokenList) {
            User user = userFindService.findById(token.getUserId());
            GetListOfPassProductByOnlineUsersResponse getListOfPassProductByOnlineUsersResponse;
            if (user.getRole() == Role.SUPERVISOR)
                continue;
            if (user.getRole() == Role.MANAGER) {
                getListOfPassProductByOnlineUsersResponse =
                        new GetListOfPassProductByOnlineUsersResponse(user.getId(), productRepository.findPassByManager(user, currentDate));
            } else if (user.getRole() == Role.PART_TIMER) {
                getListOfPassProductByOnlineUsersResponse =
                        new GetListOfPassProductByOnlineUsersResponse(user.getId(), productRepository.findPassByPartTimer(user, currentDate));
            } else throw BaseException.type(UserErrorCode.ROLE_NOT_FOUND);
            getListOfPassProductByOnlineUsersResponseList.add(getListOfPassProductByOnlineUsersResponse);
        }
        return getListOfPassProductByOnlineUsersResponseList;
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

    private void checkRequestIdHasRequestStore(Long userId, Store store) {
        User user = userFindService.findById(userId);
        if (user.getRole() == Role.SUPERVISOR)
            throw BaseException.type(GlobalErrorCode.INVALID_USER_JWT);
        else if (user.getRole() == Role.MANAGER) {
            if (storeService.findByUser(user) == store)
                return;
            throw BaseException.type(UserErrorCode.USER_STORE_MATCH_FAIL);
        } else if (user.getRole() == Role.PART_TIMER) {
            if (partTimerService.findByUser(user).getStore() == store)
                return;
            throw BaseException.type(UserErrorCode.USER_STORE_MATCH_FAIL);
        } else throw BaseException.type(UserErrorCode.ROLE_NOT_FOUND);
    }

    private void checkRequestIdHasRequestProduct(Long userId, Product product) {
        User user = userFindService.findById(userId);
        if (user.getRole() == Role.SUPERVISOR)
            throw BaseException.type(GlobalErrorCode.INVALID_USER_JWT);
        else if (user.getRole() == Role.MANAGER) {
            if (storeService.findByUser(user) == product.getStore())
                return;
            throw BaseException.type(UserErrorCode.USER_STORE_MATCH_FAIL);
        } else if (user.getRole() == Role.PART_TIMER) {
            if (partTimerService.findByUser(user).getStore() == product.getStore())
                return;
            throw BaseException.type(UserErrorCode.USER_STORE_MATCH_FAIL);
        } else throw BaseException.type(UserErrorCode.ROLE_NOT_FOUND);
    }
}
