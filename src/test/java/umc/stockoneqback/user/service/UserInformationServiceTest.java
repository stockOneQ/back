package umc.stockoneqback.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.response.LoginIdResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("User [Service Layer] -> UserInformationService 테스트")
class UserInformationServiceTest extends ServiceTest {
    @Autowired
    private UserInformationService userInformationService;

    @BeforeEach
    void setUp() {
        userRepository.save(SAEWOO.toUser());
    }

    @Nested
    @DisplayName("로그인 아이디 찾기")
    class findLoginId {
        @Test
        @DisplayName("정보가 일치하는 사용자를 찾을 수 없으면 로그인 아이디를 찾을 수 없다")
        void throwExceptionByUserNotFound() {
            // when - then
            assertThatThrownBy(() -> userInformationService.findLoginId(SAEWOO.getName() + "diff", SAEWOO.getBirth(), Email.from(SAEWOO.getEmail())))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("로그인 아이디 찾기에 성공한다")
        void success() {
            // when
            LoginIdResponse response = userInformationService.findLoginId(SAEWOO.getName(), SAEWOO.getBirth(), Email.from(SAEWOO.getEmail()));

            // then
            assertThat(response.loginId()).isEqualTo(SAEWOO.getLoginId());
        }
    }
}