package umc.stockoneqback.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("User 도메인 테스트")
class UserTest {
    @Test
    @DisplayName("User를 생성한다")
    public void createUser() {
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