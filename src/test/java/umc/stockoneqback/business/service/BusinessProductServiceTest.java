package umc.stockoneqback.business.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.service.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.UserFixture.ELLA;
import static umc.stockoneqback.fixture.UserFixture.JACK;

@DisplayName("Business [Service Layer] -> BusinessProductService 테스트")
public class BusinessProductServiceTest extends ServiceTest {
    @Autowired
    private BusinessProductService businessProductService;

    private final ProductFixture[] productFixtures = ProductFixture.values();

    private static User manager;
    private static User supervisor;
    private static Store store;

    @BeforeEach
    void setup() {
        manager = userRepository.save(ELLA.toUser());
        store = storeRepository.save(Store.createStore(Z_SIHEUNG.getName(), Z_SIHEUNG.getSector(), Z_SIHEUNG.getAddress(), manager));

        companyRepository.save(new Company("A 납품업체", "과일", "ABC123"));
        supervisor = userRepository.save(JACK.toUser());
        businessRepository.save(new Business(manager, supervisor));

        for (int i = 0; i < productFixtures.length; i++)
            productRepository.save(productFixtures[i].toProduct(store));
    }

    @Nested
    @DisplayName("사장님 가게의 제품명 검색")
    class searchProductOthers {
        @Test
        @DisplayName("사장님 가게의 제품명 검색에 성공한다")
        void success() throws IOException {
            // when - then
            List<SearchProductOthersResponse> searchProductOthersResponseList = businessProductService.searchProductOthers(
                    supervisor.getId(), manager.getId(), StoreCondition.ROOM.getValue(), productFixtures[0].getName());

            assertAll(
                    () -> assertThat(searchProductOthersResponseList.get(0).id()).isEqualTo(1L),
                    () -> assertThat(searchProductOthersResponseList.get(0).name()).isEqualTo(productFixtures[0].getName()),
                    () -> assertThat(searchProductOthersResponseList.get(0).stockQuantity()).isEqualTo(productFixtures[0].getStockQuantity())
            );
        }
    }

    @Nested
    @DisplayName("사장님 가게의 분류 기준별 제품 개수 조회")
    class getTotalProductOthers {
        @Test
        @DisplayName("사장님 가게의 분류 기준별 제품 개수 조회에 성공한다")
        void success() throws IOException {
            // when - then
            List<GetTotalProductResponse> getTotalProductResponseList = businessProductService.getTotalProductOthers(
                    supervisor.getId(), manager.getId(), StoreCondition.ROOM.getValue());

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

    @Nested
    @DisplayName("사장님 가게의 카테고리별 제품 목록 조회")
    class getListOfSearchProductOthers {
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
