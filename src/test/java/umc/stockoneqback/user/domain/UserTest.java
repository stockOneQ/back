package umc.stockoneqback.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.exception.UserErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("User 도메인 테스트")
class UserTest {
    @Nested
    @DisplayName("User를 생성한다")
    class createUser {
        @Test
        @DisplayName("존재하지 않는 역할은 역할 Enum Class로 변환할 수 없다")
        void throwExceptionByRoleNotFound() {
            org.assertj.core.api.Assertions.assertThatThrownBy(() -> User.createUser(Email.from(ANNE.getEmail()), Password.encrypt(ANNE.getPassword(), ENCODER), ANNE.getUsername(), ANNE.getBirth(), ANNE.getPhoneNumber(), "시장님"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.ROLE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("User를 생성에 성공한다")
        void success() {
            User user = SAEWOO.toUser();

            assertAll(
                    () -> assertThat(user.getEmail().getValue()).isEqualTo(SAEWOO.getEmail()),
                    () -> assertThat(user.getPassword().isSamePassword(SAEWOO.getPassword(), ENCODER)).isTrue(),
                    () -> assertThat(user.getUsername()).isEqualTo(SAEWOO.getUsername()),
                    () -> assertThat(user.getBirth()).isEqualTo(SAEWOO.getBirth()),
                    () -> assertThat(user.getPhoneNumber()).isEqualTo(SAEWOO.getPhoneNumber()),
                    () -> assertThat(user.getRole()).isEqualTo(Role.PART_TIME),
                    () -> assertThat(user.getStatus()).isTrue()
            );
        }
    }
}