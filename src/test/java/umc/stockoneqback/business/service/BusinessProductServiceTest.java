package umc.stockoneqback.business.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.exception.BusinessErrorCode;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.service.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.response.SearchProductOthersResponse;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserService;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Business [Service Layer] -> BusinessProductService 테스트")
public class BusinessProductServiceTest extends ServiceTest {
    @Autowired
    private BusinessProductService businessProductService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private final ProductFixture[] productFixtures = ProductFixture.values();
    private final Product[] products = new Product[17];
    private static Long MANAGER_ID;
    private static Store zStore;
    private static Long USER_ID;
    private static Company company;

    @BeforeEach
    void setup() {
        zStore = storeRepository.save(Z_YEONGTONG.toStore());
        MANAGER_ID = userService.saveManager(ANNE.toUser(), zStore.getId());
        for (int i = 0; i < products.length-1; i++)
            products[i] = productRepository.save(productFixtures[i].toProduct(zStore));

        company = companyRepository.save(Company.builder()
                .name("A회사")
                .sector("카페")
                .code("RANDOM")
                .build());
        USER_ID = userService.saveSupervisor(WIZ.toUser(), company.getName(), company.getCode());
        authService.login(WIZ.getLoginId(), WIZ.getPassword());

        User user = userRepository.findById(USER_ID).orElseThrow();
        User manager = userRepository.findById(MANAGER_ID).orElseThrow();
        businessRepository.save(Business.builder()
                .manager(manager)
                .supervisor(user)
                .build());
    }

    @Nested
    @DisplayName("공통 예외")
    class commonError {
        @Test
        @DisplayName("요청하는 사용자와 요청 대상이 비즈니스 관계가 아닐 경우 API 호출에 실패한다")
        void throwExceptionByInvalidFriend() {
            User user = userRepository.save(UNKNOWN.toUser());
            User manager = userRepository.findByLoginIdAndStatus(ANNE.toUser().getLoginId(), Status.NORMAL).orElseThrow();

            assertThatThrownBy(() -> businessProductService.checkRelation(user, manager.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BusinessErrorCode.BUSINESS_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("사장님 가게의 제품명 검색")
    class findProductOthers {
        @Test
        @DisplayName("사장님 가게의 제품명 검색에 성공한다")
        void success() throws IOException {
            User user = userRepository.findByLoginIdAndStatus(WIZ.toUser().getLoginId(), Status.NORMAL).orElseThrow();
            User manager = userRepository.findByLoginIdAndStatus(ANNE.toUser().getLoginId(), Status.NORMAL).orElseThrow();

            List<SearchProductOthersResponse> searchProductOthersResponseList =
                    businessProductService.searchProductOthers(user.getId(), manager.getId(),
                            products[0].getStoreCondition().getValue(), products[0].getName());

            assertAll(
                    () -> assertThat(searchProductOthersResponseList.get(0).id()).isEqualTo(products[0].getId()),
                    () -> assertThat(searchProductOthersResponseList.get(0).name()).isEqualTo(products[0].getName()),
                    () -> assertThat(searchProductOthersResponseList.get(0).stockQuant()).isEqualTo(products[0].getStockQuant())
            );
        }
    }

    @Nested
    @DisplayName("사장님 가게의 분류 기준별 제품 개수 조회")
    class findTotalProduct {
        @Test
        @DisplayName("사장님 가게의 분류 기준별 제품 개수 조회에 성공한다")
        void success() throws IOException {
            User user = userRepository.findByLoginIdAndStatus(WIZ.toUser().getLoginId(), Status.NORMAL).orElseThrow();
            User manager = userRepository.findByLoginIdAndStatus(ANNE.toUser().getLoginId(), Status.NORMAL).orElseThrow();

            List<GetTotalProductResponse> getTotalProductResponseList =
                    businessProductService.getTotalProductOthers(user.getId(), manager.getId(), products[0].getStoreCondition().getValue());

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

    @Nested
    @DisplayName("사장님 가게의 카테고리별 제품 목록 조회")
    class findListOfAllProduct {
        /*
        @Test
        @DisplayName("잘못된 카테고리명이 입력되면 사장님 가게의 카테고리별 제품 목록 조회에 실패한다")
        void throwExceptionByInvalidCategory() throws Exception {
            User user = userRepository.findByLoginIdAndStatus(WIZ.toUser().getLoginId(), Status.NORMAL).orElseThrow();
            User manager = userRepository.findByLoginIdAndStatus(ANNE.toUser().getLoginId(), Status.NORMAL).orElseThrow();

            assertThatThrownBy(() -> businessProductService.getListOfSearchProductOthers
                    (user.getId(), manager.getId(), products[0].getStoreCondition().getValue(), null, "Error"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.NOT_FOUND_CATEGORY.getMessage());
        }

        @Test
        @DisplayName("사장님 가게의 카테고리별 제품 목록 조회에 성공한다")
        void success() throws IOException {
            User user = userRepository.findByLoginIdAndStatus(WIZ.toUser().getLoginId(), Status.NORMAL).orElseThrow();
            User manager = userRepository.findByLoginIdAndStatus(ANNE.toUser().getLoginId(), Status.NORMAL).orElseThrow();

            List<SearchProductOthersResponse> allProductOthersResponseList = businessProductService.getListOfSearchProductOthers
                    (user.getId(), manager.getId(), products[0].getStoreCondition().getValue(), null, "All");
            List<SearchProductOthersResponse> passProductOthersResponseList = businessProductService.getListOfSearchProductOthers
                    (user.getId(), manager.getId(), products[0].getStoreCondition().getValue(), null, "Pass");
            List<SearchProductOthersResponse> closeProductOthersResponseList = businessProductService.getListOfSearchProductOthers
                    (user.getId(), manager.getId(), products[0].getStoreCondition().getValue(), null, "Close");
            List<SearchProductOthersResponse> lackProductOthersResponseList = businessProductService.getListOfSearchProductOthers
                    (user.getId(), manager.getId(), products[0].getStoreCondition().getValue(), null, "Lack");

            assertAll(
                    () -> assertThat(allProductOthersResponseList.get(0).name()).isEqualTo("감"),
                    () -> assertThat(allProductOthersResponseList.size()).isEqualTo(9),
                    () -> assertThat(passProductOthersResponseList.get(0).name()).isEqualTo("감"),
                    () -> assertThat(passProductOthersResponseList.size()).isEqualTo(2),
                    () -> assertThat(closeProductOthersResponseList.get(0).name()).isEqualTo("두리안"),
                    () -> assertThat(closeProductOthersResponseList.size()).isEqualTo(5),
                    () -> assertThat(lackProductOthersResponseList.get(0).name()).isEqualTo("복숭아"),
                    () -> assertThat(lackProductOthersResponseList.size()).isEqualTo(4)
            );
        }
         */
    }
}
