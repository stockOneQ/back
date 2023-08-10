package umc.stockoneqback.share.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.exception.BusinessErrorCode;
import umc.stockoneqback.business.infra.query.dto.FilteredBusinessUser;
import umc.stockoneqback.business.infra.query.dto.FindBusinessUser;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.ShareFixture;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.role.domain.store.PartTimer;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.share.domain.Share;
import umc.stockoneqback.share.exception.ShareErrorCode;
import umc.stockoneqback.share.infra.query.dto.CustomShareListPage;
import umc.stockoneqback.share.infra.query.dto.ShareList;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.ShareFixture.SHARE_5;
import static umc.stockoneqback.fixture.StoreFixture.C_COFFEE;
import static umc.stockoneqback.fixture.UserFixture.*;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("Share [Service Layer] -> ShareListService 테스트")
class ShareListServiceTest extends ServiceTest {
    @Autowired
    private ShareListService shareListService;

    private User manager1, manager2, manager3;
    private User supervisor1, supervisor2;
    private User partTimer1;
    private Business business1, business2, business3;
    private Store store1;
    private List<Share> shareList = new ArrayList<>();
    private List<Business> businessList = new ArrayList<>();
    private static final String CATEGORY = "레시피";
    private static final String SEARCH_TYPE = "내용";
    private static final String SEARCH_WORD = "쿠키";
    private static final int PAGE = 0;

    @BeforeEach
    void setUp() {
        manager1 = userRepository.save(ANNE.toUser());
        manager2 = userRepository.save(SOPHIA.toUser());
        manager3 = userRepository.save(MIKE.toUser());
        supervisor1 = userRepository.save(WIZ.toUser());
        supervisor2 = userRepository.save(OLIVIA.toUser());
        business1 = businessRepository.save(new Business(manager1, supervisor1));
        businessList.add(business1);
        business2 = businessRepository.save(new Business(manager2, supervisor1));
        businessList.add(business2);
        business3 = businessRepository.save(new Business(manager3, supervisor1));
        businessList.add(business3);

        partTimer1 = userRepository.save((BOB.toUser()));
        store1 = storeRepository.save(C_COFFEE.toStore());
        partTimerRepository.save(PartTimer.createPartTimer(store1, partTimer1));
        store1.updateStoreManager(manager2);
        store1.updateStorePartTimers(partTimer1);

        for (ShareFixture shareFixture : ShareFixture.values())
            shareList.add(shareFixture.toShare(business3));
        shareRepository.saveAll(shareList);
    }

    @Nested
    @DisplayName("유저 셀렉트박스 반환")
    class userSelectBox {
        @Test
        @DisplayName("유저의 Role이 유효하지 않다면 셀렉트박스 반환에 실패한다")
        void throwRoleNotFound() {
            // given
            User user = User.createUser(Email.from("user@naver.com"), "user123", Password.encrypt("pwd12345!", ENCODER), "유저", LocalDate.of(2000, 01, 01), "01012345678", null);
            Long invalidUserId = userRepository.save(user).getId();

            // when - then
            assertThatThrownBy(() -> shareListService.userSelectBox(invalidUserId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.ROLE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("유저 셀렉트박스 반환에 성공한다")
        void success() {
            // given - when
            FilteredBusinessUser<FindBusinessUser> filteredBusinessUser = shareListService.userSelectBox(supervisor1.getId());

            // then
            assertThat(filteredBusinessUser.getTotal()).isEqualTo(3);

            for (int i = 0; i < filteredBusinessUser.getTotal(); i++) {
                FindBusinessUser findBusinessUser = filteredBusinessUser.getBusinessUserList().get(i);
                Business business = businessList.get(i);

                assertThat(findBusinessUser.getName()).isEqualTo(business.getManager().getName());
            }
        }
    }

    @Nested
    @DisplayName("커넥트 - 자료 글 목록 반환")
    class getShareList {
        @Test
        @DisplayName("유저의 Role이 유효하지 않다면 자료 글 목록 반환에 실패한다")
        void throwRoleNotFound() {
            // given
            User user = User.createUser(Email.from("user@naver.com"), "user123", Password.encrypt("pwd12345!", ENCODER), "유저", LocalDate.of(2000, 01, 01), "01012345678", null);
            Long invalidUserId = userRepository.save(user).getId();

            // when - then
            assertThatThrownBy(() -> shareListService.getShareList(invalidUserId, business1.getId(), PAGE, CATEGORY, SEARCH_TYPE, SEARCH_WORD))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.ROLE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("연결 되어있지 않은 비즈니스 관계라면 자료 글 목록 반환에 실패한다")
        void throwBusinessNotFound() throws IOException {
            // when - then
            assertThatThrownBy(() -> shareListService.getShareList(supervisor2.getId(), business1.getId(), PAGE, CATEGORY, SEARCH_TYPE, SEARCH_WORD))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BusinessErrorCode.BUSINESS_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("셀렉트박스에 없는 유저라면 자료 글 목록 반환에 실패한다")
        void throwNotFilteredUser() throws IOException {
            // when - then
            assertThatThrownBy(() -> shareListService.getShareList(partTimer1.getId(), business1.getId(), PAGE, CATEGORY, SEARCH_TYPE, SEARCH_WORD))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ShareErrorCode.NOT_FILTERED_USER.getMessage());
        }

        @Test
        @DisplayName("커넥트 - 자료 글 목록 반환에 성공한다")
        void success() throws IOException {
            // given - when
            CustomShareListPage<ShareList> shareLists = shareListService.getShareList(manager3.getId(), business3.getId(), PAGE, CATEGORY, SEARCH_TYPE, SEARCH_WORD);

            // then
            assertAll(
                    () -> assertThat(shareLists.getShareList().get(0).getTitle()).isEqualTo(SHARE_5.getTitle()),
                    () -> assertThat(shareLists.getPageInfo().getPageNumber()).isEqualTo(0),
                    () -> assertThat(shareLists.getPageInfo().getPageSize()).isEqualTo(6),
                    () -> assertThat(shareLists.getPageInfo().getTotalPages()).isEqualTo(1),
                    () -> assertThat(shareLists.getPageInfo().getTotalElements()).isEqualTo(1)
            );
        }
    }
}