package umc.stockoneqback.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.GlobalErrorCode;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.SearchCondition;
import umc.stockoneqback.product.domain.SortCondition;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductResponse;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.*;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("Product [Service Layer] -> ProductFindService 테스트")
public class ProductFindServiceTest extends ServiceTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductFindService productFindService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private final ProductFixture[] productFixtures = ProductFixture.values();
    private final Product[] products = new Product[17];
    private final Long productEmptyId = -1L;
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
            assertThatThrownBy(() -> productFindService.getTotalProduct(USER2_ID, zStore.getId(), "상온"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_STORE_MATCH_FAIL.getMessage());
        }

        @Test
        @DisplayName("입력된 사용자가 유효하지 않은 역할을 가지고 있는 경우 서비스 호출에 실패한다")
        void throwExceptionByInvalidUser() throws Exception {
            User user = userRepository.save(User.createUser(Email.from("a@naver.com"), "a", Password.encrypt("secure123!", ENCODER),
                    "a", LocalDate.of(2001, 1, 1), "010-0000-0000", null));

            assertThatThrownBy(() -> productFindService.getTotalProduct(user.getId(), zStore.getId(), "냉동"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.ROLE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("제품명 검색")
    class findProduct {
        @Test
        @DisplayName("제품명 검색에 성공한다")
        void success() throws IOException {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<SearchProductResponse> responseList =
                    productFindService.searchProduct(USER_ID, zStore.getId(), products[0].getStoreCondition().getValue(), products[0].getName());

            assertAll(
                    () -> assertThat(responseList.get(0).id()).isEqualTo(products[0].getId()),
                    () -> assertThat(responseList.get(0).name()).isEqualTo(products[0].getName())
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
            List<GetTotalProductResponse> totalProductResponseList =
                    productFindService.getTotalProduct(USER_ID, zStore.getId(), products[0].getStoreCondition().getValue());

            assertAll(
                    () -> assertThat(totalProductResponseList.get(0).name()).isEqualTo("Total"),
                    () -> assertThat(totalProductResponseList.get(0).total()).isEqualTo(15),
                    () -> assertThat(totalProductResponseList.get(1).name()).isEqualTo("Pass"),
                    () -> assertThat(totalProductResponseList.get(1).total()).isEqualTo(2),
                    () -> assertThat(totalProductResponseList.get(2).name()).isEqualTo("Close"),
                    () -> assertThat(totalProductResponseList.get(2).total()).isEqualTo(5),
                    () -> assertThat(totalProductResponseList.get(3).name()).isEqualTo("Lack"),
                    () -> assertThat(totalProductResponseList.get(3).total()).isEqualTo(4)
            );
        }
    }

    @Nested
    @DisplayName("특정 조건을 만족하는 제품 목록 조회")
    class findListOfSearchProduct {
        @Test
        @DisplayName("특정 조건을 만족하는 목록 조회에 성공한다")
        void success() throws IOException {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<SearchProductResponse> productResponseOrderByNameList = productFindService.getListOfSearchProduct
                    (USER_ID, zStore.getId(), products[0].getStoreCondition().getValue(), SearchCondition.ALL.getValue(), productEmptyId, SortCondition.NAME.getValue());
            List<SearchProductResponse> productResponseOrderByOrderFreqList = productFindService.getListOfSearchProduct
                    (USER_ID, zStore.getId(), products[0].getStoreCondition().getValue(), SearchCondition.ALL.getValue(), productEmptyId, SortCondition.ORDER_FREQUENCY.getValue());

            assertAll(
                    () -> assertThat(productResponseOrderByNameList.get(0).name()).isEqualTo("감"),
                    () -> assertThat(productResponseOrderByNameList.size()).isEqualTo(12),
                    () -> assertThat(productResponseOrderByOrderFreqList.get(0).name()).isEqualTo("바나나"),
                    () -> assertThat(productResponseOrderByOrderFreqList.size()).isEqualTo(12)
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
}
