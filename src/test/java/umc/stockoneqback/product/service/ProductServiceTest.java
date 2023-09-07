package umc.stockoneqback.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.common.EmbeddedRedisConfig;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.product.service.dto.response.LoadProductResponse;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.ProductFixture.APPLE;
import static umc.stockoneqback.fixture.ProductFixture.CHERRY;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.UserFixture.ELLA;
import static umc.stockoneqback.fixture.UserFixture.SOPHIA;

@Import(EmbeddedRedisConfig.class)
@DisplayName("Product [Service Layer] -> ProductService 테스트")
public class ProductServiceTest extends ServiceTest {
    @Autowired
    private ProductService productService;

    private static User user;
    private static Store store;

    @BeforeEach
    void setup() {
        user = userRepository.save(SOPHIA.toUser());
        store = storeRepository.save(Z_SIHEUNG.toStore(user));
    }

    @Nested
    @DisplayName("제품 등록")
    class saveProduct {
        @Test
        @DisplayName("입력된 사용자가 입력된 가게 소속이 아니면 Product 등록에 실패한다")
        void throwExceptionByConflictUserAndStore() {
            // given
            User fakeUser = userRepository.save(ELLA.toUser());
            storeRepository.save(Z_SIHEUNG.toStore(fakeUser));

            // when - then
            assertThatThrownBy(() -> productService.saveProduct(fakeUser.getId(), store.getId(), "상온", APPLE.toProduct(store), null))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_STORE_MATCH_FAIL.getMessage());
        }

        @Test
        @DisplayName("이미 있는 제품명이면 Product 등록에 실패한다")
        void throwExceptionByAlreadyExistProduct() {
            // given
            productService.saveProduct(user.getId(), store.getId(), "상온", APPLE.toProduct(store), null);

            // when - then
            assertThatThrownBy(() -> productService.saveProduct(user.getId(), store.getId(), "상온", APPLE.toProduct(store), null))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.DUPLICATE_PRODUCT.getMessage());
        }

        @Test
        @DisplayName("제품 등록에 성공한다")
        void success() {
            // given
            Long productId = productService.saveProduct(user.getId(), store.getId(), "상온", APPLE.toProduct(store), null);

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
            Long productId = productService.saveProduct(user.getId(), store.getId(), "상온", APPLE.toProduct(store), null);

            // when
            productService.deleteProduct(user.getId(), productId);

            // then
            assertThatThrownBy(() -> productService.loadProduct(user.getId(), productId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.NOT_FOUND_PRODUCT.getMessage());
        }

        @Test
        @DisplayName("제품 상세정보 조회에 성공한다")
        void success() throws IOException {
            // given
            Long productId = productService.saveProduct(user.getId(), store.getId(), "상온", APPLE.toProduct(store), null);

            // when - then
            LoadProductResponse loadProductResponse = productService.loadProduct(user.getId(), productId);
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
    @DisplayName("제품 수정")
    class editProduct {
        @Test
        @DisplayName("제품 수정에 성공한다")
        void success() {
            // given
            Long productId = productService.saveProduct(user.getId(), store.getId(), "상온", APPLE.toProduct(store), null);

            // when
            Product findProduct = productRepository.findProductById(productId).orElseThrow();
            productService.editProduct(user.getId(), findProduct.getId(), CHERRY.toProduct(store), null);

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
            Long productId = productService.saveProduct(user.getId(), store.getId(), "상온", APPLE.toProduct(store), null);

            // when
            Product findProduct = productRepository.findById(productId).orElseThrow();
            productService.deleteProduct(user.getId(), findProduct.getId());

            assertAll(
                    () -> assertThat(findProduct.getName()).isEqualTo(APPLE.getName()),
                    () -> assertThat(findProduct.getStore()).isEqualTo(store),
                    () -> assertThat(findProduct.getStoreCondition()).isEqualTo(APPLE.getStoreCondition()),
                    () -> assertThat(findProduct.getStatus()).isEqualTo(Status.EXPIRED)
            );
        }
    }

}