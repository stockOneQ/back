package umc.stockoneqback.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.service.UserService;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.ANNE;

@DisplayName("Product [Service Layer] -> ProductOthersService 테스트")
public class ProductOthersServiceTest extends ServiceTest {
    @Autowired
    private ProductOthersService productOthersService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private final ProductFixture[] productFixtures = ProductFixture.values();
    private final Product[] products = new Product[17];
    private static Long USER_ID;
    private static Store zStore;

    @BeforeEach
    void setup() {
        zStore = storeRepository.save(Z_YEONGTONG.toStore());
        USER_ID = userService.saveManager(ANNE.toUser(), zStore.getId());
        authService.login(ANNE.getLoginId(), ANNE.getPassword());
        for (int i = 0; i < products.length-1; i++)
            products[i] = productRepository.save(productFixtures[i].toProduct(zStore));
    }

    @Nested
    @DisplayName("공통 예외")
    class commonError {
        @Test
        @DisplayName("보관방법이 유효하지 않은 값일 경우 API 호출에 실패한다")
        void throwExceptionByInvalidStoreCondition() throws Exception {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            String errorStoreCondition = "조회수";

            assertThrows(NullPointerException.class, () -> {
                productOthersService.getTotalProductOthers(zStore, errorStoreCondition);
            });
        }
    }

    @Nested
    @DisplayName("제품명 검색")
    class findProductOthers {
        @Test
        @DisplayName("제품명 검색에 성공한다")
        void success() throws IOException {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<SearchProductOthersResponse> searchProductOthersResponseList =
                    productOthersService.searchProductOthers(zStore, products[0].getStoreCondition().getValue(), products[0].getName());

            assertAll(
                    () -> assertThat(searchProductOthersResponseList.get(0).id()).isEqualTo(products[0].getId()),
                    () -> assertThat(searchProductOthersResponseList.get(0).name()).isEqualTo(products[0].getName()),
                    () -> assertThat(searchProductOthersResponseList.get(0).stockQuant()).isEqualTo(products[0].getStockQuant())
            );
        }
    }

    @Nested
    @DisplayName("분류 기준별 제품 개수 조회")
    class findTotalProduct {
        @Test
        @DisplayName("분류 기준별 제품 개수 조회에 성공한다")
        void success() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<GetTotalProductResponse> getTotalProductResponseList =
                    productOthersService.getTotalProductOthers(zStore, products[0].getStoreCondition().getValue());

            assertAll(
                    () -> assertThat(getTotalProductResponseList.get(0).name()).isEqualTo("Total"),
                    () -> assertThat(getTotalProductResponseList.get(0).total()).isEqualTo(15),
                    () -> assertThat(getTotalProductResponseList.get(1).name()).isEqualTo("Pass"),
                    () -> assertThat(getTotalProductResponseList.get(1).total()).isEqualTo(2),
                    () -> assertThat(getTotalProductResponseList.get(2).name()).isEqualTo("Close"),
                    () -> assertThat(getTotalProductResponseList.get(2).total()).isEqualTo(5),
                    () -> assertThat(getTotalProductResponseList.get(3).name()).isEqualTo("Lack"),
                    () -> assertThat(getTotalProductResponseList.get(3).total()).isEqualTo(4)
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
