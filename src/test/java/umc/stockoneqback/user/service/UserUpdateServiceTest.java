package umc.stockoneqback.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.response.UpdatePasswordResponse;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.fixture.UserFixture.WIZ;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("User [Service Layer] -> UserUpdateService 테스트")
class UserUpdateServiceTest extends ServiceTest {
    @Autowired
    private UserUpdateService userUpdateService;

    private Store store;
    private Company company;
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(SAEWOO.toUser());
        store = storeRepository.save(Z_SIHEUNG.toStore(user));

        company = companyRepository.save(createCompany("A 납품업체", "과일", "ABC123"));
    }

    @Nested
    @DisplayName("회원 정보 수정")
    class updateInformation {
        @Test
        @DisplayName("중복된 로그인 아이디가 존재한다면 회원 정보 수정에 실패한다")
        void throwExceptionByDuplicateLoginId() {
            // given
            userRepository.save(WIZ.toUser());

            // when - then
            assertThatThrownBy(() -> userUpdateService.updateInformation(user.getId(), WIZ.getName(), WIZ.getBirth(), WIZ.getEmail(), WIZ.getLoginId(), WIZ.getPassword(), WIZ.getPhoneNumber()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.DUPLICATE_LOGIN_ID.getMessage());
        }

        @Test
        @DisplayName("회원 정보 수정에 성공한다")
        void success() {
            // when
            userUpdateService.updateInformation(user.getId(), WIZ.getName(), WIZ.getBirth(), WIZ.getEmail(), WIZ.getLoginId(), WIZ.getPassword(), WIZ.getPhoneNumber());

            // then
            User findUser = userRepository.findById(user.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findUser.getName()).isEqualTo(WIZ.getName()),
                    () -> assertThat(findUser.getBirth()).isEqualTo(WIZ.getBirth()),
                    () -> assertThat(findUser.getEmail().getValue()).isEqualTo(WIZ.getEmail()),
                    () -> assertThat(findUser.getLoginId()).isEqualTo(WIZ.getLoginId()),
                    () -> assertThat(findUser.getPassword().isSamePassword(WIZ.getPassword(), ENCODER)).isTrue(),
                    () -> assertThat(findUser.getPhoneNumber()).isEqualTo(WIZ.getPhoneNumber())
            );
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 검증")
    class validateUpdatePassword {
        @Test
        @DisplayName("정보가 일치하는 사용자를 찾을 수 없으면 비밀번호 변경 검증을 할 수 없다")
        void throwExceptionByUserNotFound() {
            // when - then
            assertThatThrownBy(() -> userUpdateService.validateUpdatePassword(SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getLoginId() + "diff"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("비밀번호 변경 검증에 성공한다")
        void success() {
            // when
            UpdatePasswordResponse response = userUpdateService.validateUpdatePassword(SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getLoginId());

            // then
            assertAll(
                    () -> assertThat(response.validate()).isEqualTo(TRUE),
                    () -> assertThat(response.loginId()).isEqualTo(SAEWOO.getLoginId())
            );
        }
    }

    @Nested
    @DisplayName("비밀번호 변경")
    class updatePassword {
        @Test
        @DisplayName("로그인 아이디로 사용자를 찾을 수 없다면 비밀번호 변경에 실패한다")
        void throwExceptionByUserNotFound() {
            // when - then
            assertThatThrownBy(() -> userUpdateService.updatePassword(SAEWOO.getLoginId() + "diff", "newpassword!1", TRUE))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("비밀번호 변경 검증이 되지 않았다면 없다면 비밀번호 변경에 실패한다")
        void throwExceptionByNotAllowedUpdatePassword() {
            // when - then
            assertThatThrownBy(() -> userUpdateService.updatePassword(SAEWOO.getLoginId(), "newpassword!1", FALSE))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.NOT_ALLOWED_UPDATE_PASSWORD.getMessage());
        }

        @Test
        @DisplayName("비밀번호 변경에 성공한다")
        void success() {
            // when
            userUpdateService.updatePassword(SAEWOO.getLoginId(), "newnew!1", TRUE);

            // then
            User findUser = userRepository.findByLoginIdAndStatus(SAEWOO.getLoginId(), Status.NORMAL).orElseThrow();
            assertThat(findUser.getPassword().isSamePassword("newnew!1", ENCODER)).isTrue();
        }
    }

    private Company createCompany(String name, String sector, String code) {
        return Company.builder()
                .name(name)
                .sector(sector)
                .code(code)
                .build();
    }
}