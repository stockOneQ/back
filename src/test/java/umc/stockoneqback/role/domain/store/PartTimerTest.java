package umc.stockoneqback.role.domain.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("PartTimer 도메인 테스트")
class PartTimerTest {
    private Store store;
    private User user;

    @BeforeEach
    void setUp() {
        store = Store.createStore("스타벅스 - 광화문점", "카페", "서울시 중구");
        user = User.createUser(Email.from(SAEWOO.getEmail()), SAEWOO.getLoginId(), Password.encrypt(SAEWOO.getPassword(), ENCODER), SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getPhoneNumber(), SAEWOO.getRole());
    }

    @Test
    @DisplayName("PartTimer 생성에 성공한다")
    void createPartTimer() {
        // when
        PartTimer partTimer = PartTimer.createPartTimer(store, user);

        // then
        assertAll(
                () -> assertThat(partTimer.getPartTimer()).isEqualTo(user),
                () -> assertThat(partTimer.getStore()).isEqualTo(store)
        );
    }
}