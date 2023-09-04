package umc.stockoneqback.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.ProductSortCondition;
import umc.stockoneqback.product.domain.SearchCondition;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.service.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.dto.response.SearchProductResponse;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.ProductFixture.*;
import static umc.stockoneqback.fixture.StoreFixture.A_PASTA;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.ELLA;
import static umc.stockoneqback.fixture.UserFixture.MIKE;

@DisplayName("Product [Service Layer] -> ProductFindService 테스트")
public class ProductFindServiceTest extends ServiceTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductFindService productFindService;

    private User user;
    private Store store;

    @BeforeEach
    void setup() {
        user = userRepository.save(ELLA.toUser());
        store = storeRepository.save(Z_YEONGTONG.toStore(user));
    }

    @Nested
    @DisplayName("제품명 검색")
    class searchProduct {
        @Test
        @DisplayName("제품명 검색에 성공한다")
        void success() throws IOException {
            // given
            Long productId = productService.saveProduct(user.getId(), store.getId(), "상온", APPLE.toProduct(store), null);

            // when - then
            List<SearchProductResponse> responseList = productFindService.searchProduct(
                    user.getId(), store.getId(),
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
    @DisplayName("분류 기준별 제품 개수 조회")
    class findTotalProduct {
        @Test
        @DisplayName("입력된 사용자가 입력된 가게 소속이 아닌 경우 분류 기준별 제품 개수 조회에 실패한다")
        void throwExceptionByConflictUserAndStore() {
            // given
            User fakeUser = userRepository.save(MIKE.toUser());
            Store fakestore = storeRepository.save(A_PASTA.toStore(fakeUser));

            // when - then
            assertThatThrownBy(() -> productFindService.getTotalProduct(user.getId(), fakestore.getId(), StoreCondition.ROOM.getValue()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_STORE_MATCH_FAIL.getMessage());
        }

        @Test
        @DisplayName("분류 기준별 제품 개수 조회에 성공한다")
        void success() {
            // given
            ProductFixture[] productFixtures = ProductFixture.values();
            for (int i = 0; i < productFixtures.length; i++)
                productRepository.save(productFixtures[i].toProduct(store));

            // when - then
            List<GetTotalProductResponse> totalProductResponseList = productFindService.getTotalProduct(
                    user.getId(), store.getId(), StoreCondition.ROOM.getValue());
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

    @Nested
    @DisplayName("특정 조건을 만족하는 제품 목록 조회")
    class getListOfSearchProduct {
        @Test
        @DisplayName("특정 조건을 만족하는 목록 조회에 성공한다")
        void success() throws IOException {
            // given
            ProductFixture[] productFixtures = ProductFixture.values();
            Product[] products = new Product[productFixtures.length];
            for (int i = 0; i < products.length; i++)
                products[i] = productRepository.save(productFixtures[i].toProduct(store));

            // when - then
            List<SearchProductResponse> productResponseOrderByNameList = productFindService.getListOfSearchProduct(
                    user.getId(), store.getId(), StoreCondition.ROOM.getValue(), SearchCondition.ALL.getValue(), products[0].getId(), ProductSortCondition.NAME.getValue());
            List<SearchProductResponse> productResponseOrderByOrderFreqList = productFindService.getListOfSearchProduct(
                    user.getId(), store.getId(), StoreCondition.ROOM.getValue(), SearchCondition.ALL.getValue(), products[0].getId(), ProductSortCondition.ORDER_FREQUENCY.getValue());

            assertAll(
                    () -> assertThat(productResponseOrderByNameList.get(0).name()).isEqualTo(WATERMELON.getName()),
                    () -> assertThat(productResponseOrderByNameList.size()).isEqualTo(6),
                    () -> assertThat(productResponseOrderByOrderFreqList.get(0).name()).isEqualTo(ORANGE.getName()),
                    () -> assertThat(productResponseOrderByOrderFreqList.size()).isEqualTo(8)
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
            List<GetTotalProductResponse> totalProductResponseList = productFindService.getTotalProduct(
                    user.getId(),
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
}
