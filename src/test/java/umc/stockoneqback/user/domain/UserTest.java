package umc.stockoneqback.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.global.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("User 도메인 테스트")
class UserTest {
    @Nested
    @DisplayName("User를 생성한다")
    class createUser {
        @Test
        @DisplayName("User 생성에 성공한다")
        void success() {
            User user = User.createUser(Email.from(SAEWOO.getEmail()), SAEWOO.getLoginId(), Password.encrypt(SAEWOO.getPassword(), ENCODER), SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getPhoneNumber(), SAEWOO.getRole());

            assertAll(
                    () -> assertThat(user.getEmail().getValue()).isEqualTo(SAEWOO.getEmail()),
                    () -> assertThat(user.getPassword().isSamePassword(SAEWOO.getPassword(), ENCODER)).isTrue(),
                    () -> assertThat(user.getName()).isEqualTo(SAEWOO.getName()),
                    () -> assertThat(user.getBirth()).isEqualTo(SAEWOO.getBirth()),
                    () -> assertThat(user.getPhoneNumber()).isEqualTo(SAEWOO.getPhoneNumber()),
                    () -> assertThat(user.getRole()).isEqualTo(Role.PART_TIMER),
                    () -> assertThat(user.getStatus()).isEqualTo(Status.NORMAL)
            );
        }
    }
}