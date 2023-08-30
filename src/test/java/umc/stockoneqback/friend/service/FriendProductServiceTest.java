package umc.stockoneqback.friend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.fixture.UserFixture;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.service.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;

@DisplayName("Friend [Service Layer] -> FriendProductService 테스트")
public class FriendProductServiceTest extends ServiceTest {
    @Autowired
    private FriendProductService friendProductService;

    private final ProductFixture[] productFixtures = ProductFixture.values();

    private User user1;
    private User user2;

    private Store store1;
    private Store store2;

    @BeforeEach
    void setup() {
        store1 = storeRepository.save(Z_YEONGTONG.toStore());
        store2 = storeRepository.save(Z_SIHEUNG.toStore());

        user1 = userRepository.save(UserFixture.SAEWOO.toUser());
        user2 = userRepository.save(UserFixture.JACK.toUser());
        store1.updateManager(user1);
        store2.updateManager(user2);

        for (int i = 0; i < productFixtures.length; i++)
            productRepository.save(productFixtures[i].toProduct(store1));

        friendRepository.save(Friend.createFriend(user1, user2, RelationStatus.ACCEPT));
    }

    @Nested
    @DisplayName("친구 가게의 제품명 검색")
    class searchProductOthers {
        @Test
        @DisplayName("친구 가게의 제품명 검색에 성공한다")
        void success() throws IOException {
            // when - then
            for (int j = 0; j < productFixtures.length; j++) {
                int i = j;

                List<SearchProductOthersResponse> searchProductOthersResponseList = friendProductService.searchProductOthers(user2.getId(), user1.getId(), productFixtures[i].getStoreCondition().getValue(), productFixtures[i].getName());

                assertAll(
                        () -> assertThat(searchProductOthersResponseList.get(0).id()).isEqualTo(i + 1),
                        () -> assertThat(searchProductOthersResponseList.get(0).name()).isEqualTo(productFixtures[i].getName()),
                        () -> assertThat(searchProductOthersResponseList.get(0).stockQuantity()).isEqualTo(productFixtures[i].getStockQuantity())
                );
            }
        }
    }

    @Nested
    @DisplayName("친구 가게의 분류 기준별 제품 개수 조회")
    class getTotalProductOthers {
        @Test
        @DisplayName("친구 가게의 분류 기준별 제품 개수 조회에 성공한다")
        void success() {
            List<GetTotalProductResponse> getTotalProductResponseList = friendProductService.getTotalProductOthers(
                    user2.getId(),
                    user1.getId(),
                    StoreCondition.ROOM.getValue());

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
    @DisplayName("친구 가게의 카테고리별 제품 목록 조회")
    class findListOfAllProduct {
        @Test
        @DisplayName("잘못된 카테고리명이 입력되면 친구 가게의 카테고리별 제품 목록 조회에 실패한다")
        void throwExceptionByInvalidCategory() throws Exception {
            User user = userRepository.findByLoginIdAndStatus(ELLA.toUser().getLoginId(), Status.NORMAL).orElseThrow();
            User friend = userRepository.findByLoginIdAndStatus(ANNE.toUser().getLoginId(), Status.NORMAL).orElseThrow();

            assertThatThrownBy(() -> friendProductService.getListOfCategoryProductOthers
                    (user.getId(), friend.getId(), products[0].getStoreCondition().getValue(), null, "Error"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.NOT_FOUND_CATEGORY.getMessage());
        }

        @Test
        @DisplayName("친구 가게의 카테고리별 제품 목록 조회에 성공한다")
        void success() throws IOException {
            User user = userRepository.findByLoginIdAndStatus(ELLA.toUser().getLoginId(), Status.NORMAL).orElseThrow();
            User friend = userRepository.findByLoginIdAndStatus(ANNE.toUser().getLoginId(), Status.NORMAL).orElseThrow();

            List<SearchProductOthersResponse> allProductOthersResponseList = friendProductService.getListOfCategoryProductOthers
                    (user.getId(), friend.getId(), products[0].getStoreCondition().getValue(), null, "All");
            List<SearchProductOthersResponse> passProductOthersResponseList = friendProductService.getListOfCategoryProductOthers
                    (user.getId(), friend.getId(), products[0].getStoreCondition().getValue(), null, "Pass");
            List<SearchProductOthersResponse> closeProductOthersResponseList = friendProductService.getListOfCategoryProductOthers
                    (user.getId(), friend.getId(), products[0].getStoreCondition().getValue(), null, "Close");
            List<SearchProductOthersResponse> lackProductOthersResponseList = friendProductService.getListOfCategoryProductOthers
                    (user.getId(), friend.getId(), products[0].getStoreCondition().getValue(), null, "Lack");

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
    }
     */
}
