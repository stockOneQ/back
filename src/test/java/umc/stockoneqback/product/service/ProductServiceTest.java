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
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.product.service.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.response.LoadProductResponse;
import umc.stockoneqback.product.service.response.SearchProductResponse;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserService;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.ProductFixture.APPLE;
import static umc.stockoneqback.fixture.ProductFixture.CHERRY;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.ELLA;

@Import(EmbeddedRedisConfig.class)
@DisplayName("Product [Service Layer] -> ProductService 테스트")
public class ProductServiceTest extends ServiceTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private final String FCM_TOKEN = "examplefcmtokenblabla";

    private Long userId;
    private Store store;

    @BeforeEach
    void setup() {
        store = storeRepository.save(Z_YEONGTONG.toStore());
        
        userId = userService.saveManager(ANNE.toUser(), store.getId());
        authService.login(ANNE.getLoginId(), ANNE.getPassword());
        authService.saveFcm(userId, FCM_TOKEN);
    }

    @Nested
    @DisplayName("제품 등록")
    class saveProduct {
        @Test
        @DisplayName("입력된 사용자가 입력된 가게 소속이 아니면 Product 등록에 실패한다")
        void throwExceptionByConflictUserAndStore() {
            // given
            Store fakeStore = storeRepository.save(Z_SIHEUNG.toStore());
            Long fakeUser = userService.saveManager(ELLA.toUser(), fakeStore.getId());

            // when - then
            assertThatThrownBy(() -> productService.saveProduct(fakeUser, store.getId(), "상온", APPLE.toProduct(store), null))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_STORE_MATCH_FAIL.getMessage());
        }

        @Test
        @DisplayName("이미 있는 제품명이면 Product 등록에 실패한다")
        void throwExceptionByAlreadyExistProduct() {
            // given
            productService.saveProduct(userId, store.getId(), "상온", APPLE.toProduct(store), null);

            // when - then
            assertThatThrownBy(() -> productService.saveProduct(userId, store.getId(), "상온", APPLE.toProduct(store), null))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.DUPLICATE_PRODUCT.getMessage());
        }

        @Test
        @DisplayName("제품 등록에 성공한다")
        void success() {
            // given
            Long productId = productService.saveProduct(userId, store.getId(), "상온", APPLE.toProduct(store), null);

            // when - then
            Product findProduct = productRepository.findProductById(productId).orElseThrow();
            assertAll(
                    () -> assertThat(findProduct.getName()).isEqualTo(APPLE.getName()),
                    () -> assertThat(findProduct.getStore()).isEqualTo(store),
                    () -> assertThat(findProduct.getStoreCondition()).isEqualTo(APPLE.getStoreCondition()),
                    () -> assertThat(findProduct.getStatus()).isEqualTo(Status.NORMAL)
            );
        }
    }

    @Nested
    @DisplayName("제품 상세정보 조회")
    class loadProduct {
        @Test
        @DisplayName("삭제된 제품은 검색할 수 없다")
        void throwExceptionByRemovedProduct() {
            // given
            Long productId = productService.saveProduct(userId, store.getId(), "상온", APPLE.toProduct(store), null);

            // when
            productService.deleteProduct(userId, productId);

            // then
            assertThatThrownBy(() -> productService.loadProduct(userId, productId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.NOT_FOUND_PRODUCT.getMessage());
        }

        @Test
        @DisplayName("제품 상세정보 조회에 성공한다")
        void success() throws IOException {
            // given
            Long productId = productService.saveProduct(userId, store.getId(), "상온", APPLE.toProduct(store), null);

            // when - then
            LoadProductResponse loadProductResponse = productService.loadProduct(userId, productId);
            assertAll(
                    () -> assertThat(loadProductResponse.name()).isEqualTo(APPLE.getName()),
                    () -> assertThat(loadProductResponse.price()).isEqualTo(APPLE.getPrice()),
                    () -> assertThat(loadProductResponse.vendor()).isEqualTo(APPLE.getVendor()),
                    () -> assertThat(loadProductResponse.receivingDate()).isEqualTo(APPLE.getReceivingDate()),
                    () -> assertThat(loadProductResponse.expirationDate()).isEqualTo(APPLE.getExpirationDate()),
                    () -> assertThat(loadProductResponse.location()).isEqualTo(APPLE.getLocation()),
                    () -> assertThat(loadProductResponse.requireQuantity()).isEqualTo(APPLE.getRequireQuantity()),
                    () -> assertThat(loadProductResponse.stockQuantity()).isEqualTo(APPLE.getStockQuantity()),
                    () -> assertThat(loadProductResponse.siteToOrder()).isEqualTo(APPLE.getSiteToOrder()),
                    () -> assertThat(loadProductResponse.orderFreq()).isEqualTo(APPLE.getOrderFreq())
            );
        }
    }

    @Nested
    @DisplayName("제품명 검색")
    class searchProduct {
        @Test
        @DisplayName("제품명 검색에 성공한다")
        void success() throws IOException {
            // given
            Long productId = productService.saveProduct(userId, store.getId(), "상온", APPLE.toProduct(store), null);

            // when - then
            List<SearchProductResponse> responseList = productService.searchProduct(
                    userId, store.getId(),
                    APPLE.getStoreCondition().getValue(),
                    APPLE.getName()
            );
            assertAll(
                    () -> assertThat(responseList.get(0).id()).isEqualTo(productId),
                    () -> assertThat(responseList.get(0).name()).isEqualTo(APPLE.getName())
            );
        }
    }

    @Nested
    @DisplayName("제품 수정")
    class editProduct {
        @Test
        @DisplayName("제품 수정에 성공한다")
        void success() {
            // given
            Long productId = productService.saveProduct(userId, store.getId(), "상온", APPLE.toProduct(store), null);

            // when
            Product findProduct = productRepository.findProductById(productId).orElseThrow();
            productService.editProduct(userId, findProduct.getId(), CHERRY.toProduct(store), null);

            // then
            assertAll(
                    () -> assertThat(findProduct.getName()).isEqualTo(CHERRY.getName()),
                    () -> assertThat(findProduct.getStore()).isEqualTo(store),
                    () -> assertThat(findProduct.getStoreCondition()).isEqualTo(CHERRY.getStoreCondition()),
                    () -> assertThat(findProduct.getStatus()).isEqualTo(Status.NORMAL)
            );
        }
    }

    @Nested
    @DisplayName("제품 삭제")
    class deleteProduct {
        @Test
        @DisplayName("제품 삭제에 성공한다")
        void success() {
            // given
            Long productId = productService.saveProduct(userId, store.getId(), "상온", APPLE.toProduct(store), null);

            // when
            Product findProduct = productRepository.findById(productId).orElseThrow();
            productService.deleteProduct(userId, findProduct.getId());

            assertAll(
                    () -> assertThat(findProduct.getName()).isEqualTo(APPLE.getName()),
                    () -> assertThat(findProduct.getStore()).isEqualTo(store),
                    () -> assertThat(findProduct.getStoreCondition()).isEqualTo(APPLE.getStoreCondition()),
                    () -> assertThat(findProduct.getStatus()).isEqualTo(Status.EXPIRED)
            );
        }
    }

    @Nested
    @DisplayName("분류 기준별 제품 개수 조회")
    class getTotalProduct {
        @Test
        @DisplayName("분류 기준별 제품 개수 조회에 성공한다")
        void success() {
            // given
            ProductFixture[] productFixtures = ProductFixture.values();
            Product[] products = new Product[productFixtures.length];
            for (int i = 0; i < products.length; i++)
                products[i] = productRepository.save(productFixtures[i].toProduct(store));

            // when - then
            List<GetTotalProductResponse> totalProductResponseList = productService.getTotalProduct(
                    userId,
                    store.getId(),
                    APPLE.getStoreCondition().getValue()
            );
            assertAll(
                    () -> assertThat(totalProductResponseList.get(0).name()).isEqualTo("Total"),
                    () -> assertThat(totalProductResponseList.get(0).total()).isEqualTo(16),
                    () -> assertThat(totalProductResponseList.get(1).name()).isEqualTo("Pass"),
                    () -> assertThat(totalProductResponseList.get(1).total()).isEqualTo(2),
                    () -> assertThat(totalProductResponseList.get(2).name()).isEqualTo("Close"),
                    () -> assertThat(totalProductResponseList.get(2).total()).isEqualTo(5),
                    () -> assertThat(totalProductResponseList.get(3).name()).isEqualTo("Lack"),
                    () -> assertThat(totalProductResponseList.get(3).total()).isEqualTo(5)
            );
        }
    }

    /*
    @Nested
    @DisplayName("전체 제품 목록 조회")
    class findListOfAllProduct {
        @Test
        @DisplayName("전체 제품 목록 조회에 성공한다")
        void success() throws IOException {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<SearchProductResponse> productResponseOrderByNameList = productService.getListOfAllProduct
                    (userId, zStore.getId(), products[0].getStoreCondition().getValue(), null, SortCondition.NAME.getValue());
            List<SearchProductResponse> productResponseOrderByOrderFreqList = productService.getListOfAllProduct
                    (userId, zStore.getId(), products[0].getStoreCondition().getValue(), null, SortCondition.ORDER_FREQUENCY.getValue());

            assertAll(
                    () -> assertThat(productResponseOrderByNameList.get(0).name()).isEqualTo("감"),
                    () -> assertThat(productResponseOrderByNameList.size()).isEqualTo(12),
                    () -> assertThat(productResponseOrderByOrderFreqList.get(0).name()).isEqualTo("바나나"),
                    () -> assertThat(productResponseOrderByOrderFreqList.size()).isEqualTo(12)
            );
        }
    }

    @Nested
    @DisplayName("유통기한이 경과한 제품 목록 조회")
    class findListOfPassProduct {
        @Test
        @DisplayName("유통기한이 경과한 제품 목록 조회에 성공한다")
        void success() throws IOException {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<SearchProductResponse> productResponseOrderByNameList = productService.getListOfPassProduct
                    (userId, zStore.getId(), products[0].getStoreCondition().getValue(), null, SortCondition.NAME.getValue());
            List<SearchProductResponse> productResponseOrderByOrderFreqList = productService.getListOfPassProduct
                    (userId, zStore.getId(), products[0].getStoreCondition().getValue(), null, SortCondition.ORDER_FREQUENCY.getValue());

            Product firstOrderFreq = productRepository.findById(productResponseOrderByOrderFreqList.get(0).id()).orElseThrow();
            Product secondOrderFreq = productRepository.findById(productResponseOrderByOrderFreqList.get(1).id()).orElseThrow();

            assertAll(
                    () -> assertThat(productResponseOrderByNameList.get(0).name())
                            .isLessThan(productResponseOrderByNameList.get(1).name()),
                    () -> assertThat(productResponseOrderByNameList.size()).isEqualTo(2),
                    () -> assertThat(firstOrderFreq.getOrderFreq()).isGreaterThan(secondOrderFreq.getOrderFreq()),
                    () -> assertThat(productResponseOrderByOrderFreqList.size()).isEqualTo(2)
            );
        }
    }

    @Nested
    @DisplayName("유통기한이 임박한 제품 목록 조회")
    class findListOfCloseProduct {
        @Test
        @DisplayName("유통기한이 임박한 제품 목록 조회에 성공한다")
        void success() throws IOException {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<SearchProductResponse> productResponseOrderByNameList = productService.getListOfCloseProduct
                    (userId, zStore.getId(), products[0].getStoreCondition().getValue(), null, SortCondition.NAME.getValue());
            List<SearchProductResponse> productResponseOrderByOrderFreqList = productService.getListOfCloseProduct
                    (userId, zStore.getId(), products[0].getStoreCondition().getValue(), null, SortCondition.ORDER_FREQUENCY.getValue());

            assertAll(
                    () -> assertThat(productResponseOrderByNameList.get(1).name()).isEqualTo("메론"),
                    () -> assertThat(productResponseOrderByNameList.size()).isEqualTo(5),
                    () -> assertThat(productResponseOrderByOrderFreqList.get(1).name()).isEqualTo("사과"),
                    () -> assertThat(productResponseOrderByOrderFreqList.size()).isEqualTo(5)
            );
        }
    }

    @Nested
    @DisplayName("재고가 부족한 제품 목록 조회")
    class findListOfLackProduct {
        @Test
        @DisplayName("재고가 부족한 제품 목록 조회에 성공한다")
        void success() throws IOException {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<SearchProductResponse> productResponseOrderByNameList = productService.getListOfLackProduct
                    (userId, zStore.getId(), products[0].getStoreCondition().getValue(), null, SortCondition.NAME.getValue());
            List<SearchProductResponse> productResponseOrderByOrderFreqList = productService.getListOfLackProduct
                    (userId, zStore.getId(), products[0].getStoreCondition().getValue(), null, SortCondition.ORDER_FREQUENCY.getValue());

            assertAll(
                    () -> assertThat(productResponseOrderByNameList.get(0).name()).isEqualTo("복숭아"),
                    () -> assertThat(productResponseOrderByNameList.size()).isEqualTo(4),
                    () -> assertThat(productResponseOrderByOrderFreqList.get(0).name()).isEqualTo("파인애플"),
                    () -> assertThat(productResponseOrderByOrderFreqList.size()).isEqualTo(4)
            );
        }
    }

    @Nested
    @DisplayName("조회 시 보관방법 및 정렬조건 검증")
    class checkConditionWhenFindProduct {
        @Test
        @DisplayName("존재하지 않는 보관방법이 입력되면 예외가 발생한다")
        void throwExceptionByWrongStoreCondition() {
            try{
                StoreCondition.findStoreConditionByValue("고온");
            } catch (BaseException e) {
                assertEquals(e.getMessage(), ProductErrorCode.NOT_FOUND_STORE_CONDITION.getMessage());
            }
        }

        @Test
        @DisplayName("존재하지 않는 정렬조건이 입력되면 예외가 발생한다")
        void throwExceptionByWrongSortCondition() {
            try{
                SortCondition.findSortConditionByValue("가격");
            } catch (BaseException e) {
                assertEquals(e.getMessage(), ProductErrorCode.NOT_FOUND_SORT_CONDITION.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("현재 접속중인 사용자별 유통기한 경과 제품 목록 조회")
    class getListOfPassProductByOnlineUsers {
        @Test
        @DisplayName("현재 접속중인 사용자별 유통기한 경과 제품 목록 조회에 성공한다")
        void success() {
            List<FcmToken> tokenList = tokenService.findAllOnlineUsers();
            User user = userFindService.findById(tokenList.get(0).getId());
            List<Product> getListOfPassProductByOnlineUsersResponse = productRepository.findPassByManager(user, LocalDate.now());

            assertAll(
                    () -> assertThat(user.getId()).isEqualTo(userId),
                    () -> assertThat(tokenList.get(0).getToken()).isEqualTo(FCM_TOKEN),
                    () -> assertThat(getListOfPassProductByOnlineUsersResponse.size()).isEqualTo(2),
                    () -> assertThat(getListOfPassProductByOnlineUsersResponse.get(0).getName()).isEqualTo("감"),
                    () -> assertThat(getListOfPassProductByOnlineUsersResponse.get(1).getName()).isEqualTo("포도")
            );
        }
    }
     */
}
