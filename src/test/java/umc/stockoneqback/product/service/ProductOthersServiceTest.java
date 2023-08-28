package umc.stockoneqback.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.service.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.response.SearchProductOthersResponse;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.TONY;

@DisplayName("Product [Service Layer] -> ProductOthersService 테스트")
public class ProductOthersServiceTest extends ServiceTest {
    @Autowired
    private ProductOthersService productOthersService;

    private final ProductFixture[] productFixtures = ProductFixture.values();

    private static User user;
    private static Store store;

    @BeforeEach
    void setup() {
        store = storeRepository.save(Z_YEONGTONG.toStore());
        user = userRepository.save(TONY.toUser());
        store.updateManager(user);

        for (int i = 0; i < productFixtures.length; i++)
            productRepository.save(productFixtures[i].toProduct(store));
    }

    @Nested
    @DisplayName("제품명 검색")
    class searchProductOthers {
        @Test
        @DisplayName("제품명 검색에 성공한다")
        void success() throws IOException {
            for (int i = 0; i < productFixtures.length; i++) {
                int j = i;
                List<SearchProductOthersResponse> searchProductOthersResponseList = productOthersService.searchProductOthers(
                        store, productFixtures[j].getStoreCondition().getValue(), productFixtures[j].getName());

                assertAll(
                        () -> assertThat(searchProductOthersResponseList.get(0).id()).isEqualTo(j + 1),
                        () -> assertThat(searchProductOthersResponseList.get(0).name()).isEqualTo(productFixtures[j].getName()),
                        () -> assertThat(searchProductOthersResponseList.get(0).stockQuantity()).isEqualTo(productFixtures[j].getStockQuantity())
                );
            }

        }
    }

    @Nested
    @DisplayName("분류 기준별 제품 개수 조회")
    class getTotalProductOthers {
        @Test
        @DisplayName("보관방법이 유효하지 않은 값일 경우 분류 기준별 제품 개수 조회에 실패한다")
        void throwExceptionByInvalidStoreCondition() {
            assertThatThrownBy(() -> productOthersService.getTotalProductOthers(store, "wrong" + StoreCondition.ROOM))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("분류 기준별 제품 개수 조회에 성공한다")
        void success() {
            List<GetTotalProductResponse> getTotalProductResponseList = productOthersService.getTotalProductOthers(
                    store, StoreCondition.ROOM.getValue());

            assertAll(
                    () -> assertThat(getTotalProductResponseList.get(0).name()).isEqualTo("Total"),
                    () -> assertThat(getTotalProductResponseList.get(0).total()).isEqualTo(16),
                    () -> assertThat(getTotalProductResponseList.get(1).name()).isEqualTo("Pass"),
                    () -> assertThat(getTotalProductResponseList.get(1).total()).isEqualTo(2),
                    () -> assertThat(getTotalProductResponseList.get(2).name()).isEqualTo("Close"),
                    () -> assertThat(getTotalProductResponseList.get(2).total()).isEqualTo(5),
                    () -> assertThat(getTotalProductResponseList.get(3).name()).isEqualTo("Lack"),
                    () -> assertThat(getTotalProductResponseList.get(3).total()).isEqualTo(5)
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
            List<SearchProductOthersResponse> productOthersResponseList = productOthersService.getListOfAllProductOthers
                    (zStore, products[0].getStoreCondition().getValue(), null);

            assertAll(
                    () -> assertThat(productOthersResponseList.get(0).name()).isEqualTo("감"),
                    () -> assertThat(productOthersResponseList.get(0).name())
                            .isLessThan(productOthersResponseList.get(1).name()),
                    () -> assertThat(productOthersResponseList.size()).isEqualTo(9)
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
            List<SearchProductOthersResponse> productOthersResponseList = productOthersService.getListOfPassProductOthers
                    (zStore, products[0].getStoreCondition().getValue(), null);

            assertAll(
                    () -> assertThat(productOthersResponseList.get(0).name())
                            .isLessThan(productOthersResponseList.get(1).name()),
                    () -> assertThat(productOthersResponseList.size()).isEqualTo(2)
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
            List<SearchProductOthersResponse> productOthersResponseList = productOthersService.getListOfCloseProductOthers
                    (zStore, products[0].getStoreCondition().getValue(), null);

            assertAll(
                    () -> assertThat(productOthersResponseList.get(1).name()).isEqualTo("메론"),
                    () -> assertThat(productOthersResponseList.get(0).name())
                            .isLessThan(productOthersResponseList.get(1).name()),
                    () -> assertThat(productOthersResponseList.size()).isEqualTo(5)
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
            List<SearchProductOthersResponse> productOthersResponseList = productOthersService.getListOfLackProductOthers
                    (zStore, products[0].getStoreCondition().getValue(), null);

            assertAll(
                    () -> assertThat(productOthersResponseList.get(0).name()).isEqualTo("복숭아"),
                    () -> assertThat(productOthersResponseList.get(0).name())
                            .isLessThan(productOthersResponseList.get(1).name()),
                    () -> assertThat(productOthersResponseList.size()).isEqualTo(4)
            );
        }
    }
     */
}
