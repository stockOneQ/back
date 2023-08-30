package umc.stockoneqback.product.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.auth.domain.FcmToken;
import umc.stockoneqback.auth.service.TokenService;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.exception.GlobalErrorCode;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.product.infra.query.dto.ProductFindPage;
import umc.stockoneqback.product.service.dto.response.GetRequiredInfoResponse;
import umc.stockoneqback.product.service.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.dto.response.LoadProductResponse;
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
    private final PassProductFCMService passProductFCMService;

    @Transactional
    public GetRequiredInfoResponse getRequiredInfo(Long userId) {
        User user = userFindService.findById(userId);
        Store store = null;

        if (user.getRole() == Role.MANAGER) {
            store = storeService.findByUser(user);
        } else if (user.getRole() == Role.PART_TIMER) {
            store = partTimerService.findByUser(user).getStore();
        }

        return new GetRequiredInfoResponse(userId, store.getId());
    }

    @Transactional
    public Long saveProduct(Long userId, Long storeId, String storeConditionValue, Product product, MultipartFile image) {
        Store store = storeService.findById(storeId);
        checkRequestIdHasRequestStore(userId, store);
        StoreCondition storeCondition = StoreCondition.from(storeConditionValue);
        isExistProductByName(store, storeCondition, product.getName());

        String imageUrl = null;
        if (image != null)
            imageUrl = fileService.uploadProductFiles(image);
        product.saveStoreAndStoreConditionAndImageUrl(storeCondition, store, imageUrl);

        return productRepository.save(product).getId();
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
                .requireQuantity(product.getRequireQuant())
                .stockQuantity(product.getStockQuant())
                .siteToOrder(product.getSiteToOrder())
                .orderFreq(product.getOrderFreq())
                .build();
    }

    @Transactional
    public void editProduct(Long userId, Long productId, Product newProduct, MultipartFile image) {
        Product product = findProductById(productId);
        checkRequestIdHasRequestProduct(userId, product);
        String imageUrl = product.getImageUrl();
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
    public void pushAlarmOfPassProductByOnlineUsers() throws FirebaseMessagingException {
        List<FcmToken> fcmTokenList = tokenService.findAllOnlineUsers();
        LocalDate currentDate = LocalDate.now();
        for (FcmToken fcmToken: fcmTokenList) {
            User user = userFindService.findById(fcmToken.getId());
            if (user.getRole() == Role.SUPERVISOR || user.getRole() == Role.ADMINISTRATOR)
                continue;
            if (user.getRole() == Role.MANAGER) {
                List<Product> productList = productRepository.findPassByManager(user, currentDate);
                for (Product product: productList) {
                    passProductFCMService.sendNotification
                            (fcmToken.getToken(), product.getStoreCondition().getValue(), product.getName());
                }
            } else if (user.getRole() == Role.PART_TIMER) {
                List<Product> productList = productRepository.findPassByPartTimer(user, currentDate);
                for (Product product: productList) {
                    passProductFCMService.sendNotification
                            (fcmToken.getToken(), product.getStoreCondition().getValue(), product.getName());
                }
            } else throw BaseException.type(UserErrorCode.ROLE_NOT_FOUND);
        }
    }

    private void isExistProductByName(Store store, StoreCondition storeCondition, String name) {
        Optional<Product> isExist = productRepository.isExistProductByName(store, storeCondition.getValue(), name);
        if (isExist.isPresent())
            throw BaseException.type(ProductErrorCode.DUPLICATE_PRODUCT);
    }

    List<ProductFindPage> findProductAllByName(Store store, StoreCondition storeCondition, String productName) {
        List<ProductFindPage> searchProductUrlList = productRepository.findProductByName(store, storeCondition, productName);
        if (searchProductUrlList.isEmpty())
            throw BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT);
        return searchProductUrlList;
    }

    public Product findProductById(Long productId) {
        return productRepository.findProductById(productId)
                .orElseThrow(() -> BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT));
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

    Product configPaging(Long productId) {
        if (productId == -1)
            return new Product();
        return findProductById(productId);
    }

    byte[] getImageOrElseNull(String imageUrl) throws IOException {
        if (imageUrl == null)
            return null;
        return fileService.downloadToResponseDto(imageUrl);
    }

    public void checkRequestIdHasRequestStore(Long userId, Store store) {
        User user = userFindService.findById(userId);
        if (user.getRole() == Role.SUPERVISOR)
            throw BaseException.type(GlobalErrorCode.INVALID_USER);
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
            throw BaseException.type(GlobalErrorCode.INVALID_USER);
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
