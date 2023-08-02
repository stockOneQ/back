package umc.stockoneqback.friend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.exception.FriendErrorCode;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.GlobalErrorCode;
import umc.stockoneqback.global.utils.PasswordEncoderUtils;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;
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
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Friend [Service Layer] -> FriendProductService 테스트")
public class FriendProductServiceTest extends ServiceTest {
    @Autowired
    private FriendProductService friendProductService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private final ProductFixture[] productFixtures = ProductFixture.values();
    private final Product[] products = new Product[17];
    private static Long FRIEND_ID;
    private static Store zStore;
    private static Long USER_ID;
    private static Store userStore;

    @BeforeEach
    void setup() {
        zStore = storeRepository.save(Z_YEONGTONG.toStore());
        FRIEND_ID = userService.saveManager(ANNE.toUser(), zStore.getId());
        for (int i = 0; i < products.length-1; i++)
            products[i] = productRepository.save(productFixtures[i].toProduct(zStore));

        userStore = storeRepository.save(Z_SIHEUNG.toStore());
        USER_ID = userService.saveManager(ELLA.toUser(), userStore.getId());
        authService.login(ELLA.getLoginId(), ELLA.getPassword());

        User user = userRepository.findById(USER_ID).orElseThrow();
        User friend = userRepository.findById(FRIEND_ID).orElseThrow();
        friendRepository.save(Friend.createFriend(user, friend, FriendStatus.ACCEPT));
    }

    @Nested
    @DisplayName("공통 예외")
    class commonError {
        @Test
        @DisplayName("요청하는 사용자가 사장이 아닌 다른 역할일 경우 API 호출에 실패한다")
        void throwExceptionByInvalidRole() throws Exception {
            User user = userRepository.save(WIZ.toUser());

            assertThatThrownBy(() -> friendProductService.isManager(user.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(GlobalErrorCode.INVALID_USER_JWT.getMessage());
        }

        @Test
        @DisplayName("요청하는 사용자가 존재하지 않는 역할일 경우 API 호출에 실패한다")
        void throwExceptionByNotFoundRole() throws Exception {
            User invalidUser = userRepository.save(User.builder()
                    .email(Email.from("invaliduser@google.com"))
                    .loginId("invaliduser")
                    .password(Password.encrypt("Secure5678!", PasswordEncoderUtils.ENCODER))
                    .username("invaliduser1")
                    .phoneNumber("01000001111")
                    .birth(LocalDate.of(2000, 1, 1))
                    .role(null)
                    .build());

            assertThatThrownBy(() -> friendProductService.isManager(invalidUser.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.ROLE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("요청하는 사용자와 요청 대상이 친구 관계가 아닐 경우 API 호출에 실패한다")
        void throwExceptionByInvalidFriend() throws Exception {
            User user = userRepository.save(WIZ.toUser());
            User friend = userRepository.findByLoginId(ELLA.toUser().getLoginId()).orElseThrow();

            assertThatThrownBy(() -> friendProductService.checkRelation(user, friend.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.FRIEND_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("친구 가게의 제품명 검색")
    class findProductOthers {
        @Test
        @DisplayName("친구 가게의 제품명 검색에 성공한다")
        void success() throws IOException {
            User user = userRepository.findByLoginId(ELLA.toUser().getLoginId()).orElseThrow();
            User friend = userRepository.findByLoginId(ANNE.toUser().getLoginId()).orElseThrow();

            List<SearchProductOthersResponse> searchProductOthersResponseList =
                    friendProductService.searchProductOthers(user.getId(), friend.getId(),
                            products[0].getStoreCondition().getValue(), products[0].getName());

            assertAll(
                    () -> assertThat(searchProductOthersResponseList.get(0).id()).isEqualTo(products[0].getId()),
                    () -> assertThat(searchProductOthersResponseList.get(0).name()).isEqualTo(products[0].getName()),
                    () -> assertThat(searchProductOthersResponseList.get(0).stockQuant()).isEqualTo(products[0].getStockQuant())
            );
        }
    }

    @Nested
    @DisplayName("친구 가게의 분류 기준별 제품 개수 조회")
    class findTotalProduct {
        @Test
        @DisplayName("친구 가게의 분류 기준별 제품 개수 조회에 성공한다")
        void success() throws IOException {
            User user = userRepository.findByLoginId(ELLA.toUser().getLoginId()).orElseThrow();
            User friend = userRepository.findByLoginId(ANNE.toUser().getLoginId()).orElseThrow();

            List<GetTotalProductResponse> getTotalProductResponseList =
                    friendProductService.getTotalProductOthers(user.getId(), friend.getId(), products[0].getStoreCondition().getValue());

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
    @DisplayName("친구 가게의 카테고리별 제품 목록 조회")
    class findListOfAllProduct {
        @Test
        @DisplayName("잘못된 카테고리명이 입력되면 친구 가게의 카테고리별 제품 목록 조회에 실패한다")
        void throwExceptionByInvalidCategory() throws Exception {
            User user = userRepository.findByLoginId(ELLA.toUser().getLoginId()).orElseThrow();
            User friend = userRepository.findByLoginId(ANNE.toUser().getLoginId()).orElseThrow();

            assertThatThrownBy(() -> friendProductService.getListOfCategoryProductOthers
                    (user.getId(), friend.getId(), products[0].getStoreCondition().getValue(), null, "Error"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.NOT_FOUND_CATEGORY.getMessage());
        }

        @Test
        @DisplayName("친구 가게의 카테고리별 제품 목록 조회에 성공한다")
        void success() throws IOException {
            User user = userRepository.findByLoginId(ELLA.toUser().getLoginId()).orElseThrow();
            User friend = userRepository.findByLoginId(ANNE.toUser().getLoginId()).orElseThrow();

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
}
