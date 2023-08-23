package umc.stockoneqback.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.common.EmbeddedRedisConfig;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.GlobalErrorCode;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.dto.response.LoadProductResponse;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;
import umc.stockoneqback.user.service.UserService;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.*;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@Import(EmbeddedRedisConfig.class)
@DisplayName("Product [Service Layer] -> ProductService 테스트")
public class ProductServiceTest extends ServiceTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserFindService userFindService;

    @Autowired
    private AuthService authService;

    private final ProductFixture[] productFixtures = ProductFixture.values();
    private final Product[] products = new Product[17];
    private static final String FCM_TOKEN = "examplefcmtokenblabla";
    private static Long USER_ID;
    private static Store zStore;

    @BeforeEach
    void setup() {
        zStore = storeRepository.save(Z_YEONGTONG.toStore());
        USER_ID = userService.saveManager(ANNE.toUser(), zStore.getId());
        authService.login(ANNE.getLoginId(), ANNE.getPassword());
        authService.saveFcm(USER_ID, FCM_TOKEN);
        for (int i = 0; i < products.length-1; i++)
            products[i] = productRepository.save(productFixtures[i].toProduct(zStore));
    }

    @Nested
    @DisplayName("공통 예외")
    class commonError {
        @Test
        @DisplayName("권한이 없는 사용자가 Product 서비스를 호출한 경우 서비스 호출에 실패한다")
        void throwExceptionByUnauthorizedUser() throws Exception {
            User supervisor = WIZ.toUser();
            userRepository.save(supervisor);

            assertThatThrownBy(() -> productService.getRequiredInfo(supervisor.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(GlobalErrorCode.INVALID_USER_JWT.getMessage());
        }

        @Test
        @DisplayName("입력된 사용자가 입력된 가게 소속이 아닌 경우 서비스 호출에 실패한다")
        void throwExceptionByConflictUserAndStore() throws Exception {
            Store zStore2 = storeRepository.save(Z_SIHEUNG.toStore());
            Long USER2_ID = userService.saveManager(ELLA.toUser(), zStore2.getId());

            assertThatThrownBy(() -> productService.saveProduct(USER2_ID, zStore.getId(), "상온", products[16], null))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_STORE_MATCH_FAIL.getMessage());
        }

        @Test
        @DisplayName("입력된 사용자가 유효하지 않은 역할을 가지고 있는 경우 서비스 호출에 실패한다")
        void throwExceptionByInvalidUser() throws Exception {
            User user = userRepository.save(User.createUser(Email.from("a@naver.com"), "a", Password.encrypt("secure123!", ENCODER),
                    "a", LocalDate.of(2001, 1, 1), "010-0000-0000", null));

            assertThatThrownBy(() -> productService.saveProduct(user.getId(), zStore.getId(), "냉동", products[16], null))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.ROLE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("메인 호출")
    class getRequiredInfo {
        @Test
        @DisplayName("메인 호출에 성공한다")
        void success() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            User user = userFindService.findById(USER_ID);
            assertThat(storeService.findByUser(user)).isEqualTo(zStore);
        }
    }

    @Nested
    @DisplayName("제품 등록")
    class createProduct {
        @Test
        @DisplayName("이미 있는 제품명이면 생성에 실패한다")
        void throwExceptionByAlreadyExistProduct() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            products[16] = productFixtures[16].toProduct(zStore);
            productService.saveProduct(USER_ID, zStore.getId(), "상온", products[16], null);

            assertThatThrownBy(() -> productService.saveProduct(USER_ID, zStore.getId(), "상온", products[16], null))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.DUPLICATE_PRODUCT.getMessage());
        }

        @Test
        @DisplayName("제품 등록에 성공한다")
        void success() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            products[16] = productFixtures[16].toProduct(zStore);
            productService.saveProduct(USER_ID, zStore.getId(), "상온", products[16], null);
            Product findProduct = productRepository.findProductById(products[16].getId()).orElseThrow();

            assertAll(
                    () -> assertThat(findProduct.getName()).isEqualTo(products[16].getName()),
                    () -> assertThat(findProduct.getStore()).isEqualTo(products[16].getStore()),
                    () -> assertThat(findProduct.getStoreCondition()).isEqualTo(products[16].getStoreCondition()),
                    () -> assertThat(findProduct.getStatus()).isEqualTo(Status.NORMAL)
            );
        }
    }

    @Nested
    @DisplayName("제품 상세정보 조회")
    class displayProduct {
        @Test
        @DisplayName("제품 상세정보 조회에 성공한다")
        void success() throws IOException {
            LoadProductResponse loadProductResponse = productService.loadProduct(USER_ID, products[0].getId());

            assertAll(
                    () -> assertThat(loadProductResponse.name()).isEqualTo(products[0].getName()),
                    () -> assertThat(loadProductResponse.price()).isEqualTo(products[0].getPrice()),
                    () -> assertThat(loadProductResponse.vendor()).isEqualTo(products[0].getVendor()),
                    () -> assertThat(loadProductResponse.receivingDate()).isEqualTo(products[0].getReceivingDate()),
                    () -> assertThat(loadProductResponse.expirationDate()).isEqualTo(products[0].getExpirationDate()),
                    () -> assertThat(loadProductResponse.location()).isEqualTo(products[0].getLocation()),
                    () -> assertThat(loadProductResponse.requireQuant()).isEqualTo(products[0].getRequireQuant()),
                    () -> assertThat(loadProductResponse.stockQuant()).isEqualTo(products[0].getStockQuant()),
                    () -> assertThat(loadProductResponse.siteToOrder()).isEqualTo(products[0].getSiteToOrder()),
                    () -> assertThat(loadProductResponse.orderFreq()).isEqualTo(products[0].getOrderFreq())
            );
        }
    }

    @Nested
    @DisplayName("제품 수정")
    class changeProduct {
        @Test
        @DisplayName("제품 수정에 성공한다")
        void success() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            products[16] = productFixtures[16].toProduct(zStore);
            Long productId = products[0].getId();
            Product changeProduct = productRepository.findProductById(productId).orElseThrow();
            productService.editProduct(USER_ID, changeProduct.getId(), products[16], null);
            Product findProduct = productRepository.findProductById(productId).orElseThrow();

            assertAll(
                    () -> assertThat(findProduct.getName()).isEqualTo(products[16].getName()),
                    () -> assertThat(findProduct.getStore()).isEqualTo(products[16].getStore()),
                    () -> assertThat(findProduct.getStoreCondition()).isEqualTo(products[16].getStoreCondition()),
                    () -> assertThat(findProduct.getStatus()).isEqualTo(Status.NORMAL)
            );
        }
    }

    @Nested
    @DisplayName("제품 삭제")
    class removeProduct {
        @Test
        @DisplayName("제품 삭제에 성공한다")
        void success() {
            Long productId = products[0].getId();
            Product eraseProduct = productRepository.findProductById(productId).orElseThrow();
            productService.deleteProduct(USER_ID, eraseProduct.getId());
            Product findProduct = productRepository.findById(productId).orElseThrow();

            assertAll(
                    () -> assertThat(findProduct.getName()).isEqualTo(products[0].getName()),
                    () -> assertThat(findProduct.getStore()).isEqualTo(products[0].getStore()),
                    () -> assertThat(findProduct.getStoreCondition()).isEqualTo(products[0].getStoreCondition()),
                    () -> assertThat(findProduct.getStatus()).isEqualTo(Status.EXPIRED)
            );
        }

        @Test
        @DisplayName("삭제된 제품은 검색할 수 없다")
        void throwExceptionByRemovedProduct() {
            Long productId = products[0].getId();
            productService.deleteProduct(USER_ID, productId);

            assertThatThrownBy(() -> productService.loadProduct(USER_ID, productId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.NOT_FOUND_PRODUCT.getMessage());
        }
    }
}
