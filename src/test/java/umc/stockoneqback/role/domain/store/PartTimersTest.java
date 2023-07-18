package umc.stockoneqback.role.domain.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("PartTimers 도메인 테스트")
class PartTimersTest {
    private Store store;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        store = Store.createStore("스타벅스 - 광화문점", "카페", "서울시 중구");
        user1 = User.createUser(Email.from(SAEWOO.getEmail()), SAEWOO.getLoginId(), Password.encrypt(SAEWOO.getPassword(), ENCODER), SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getPhoneNumber(), SAEWOO.getRole());
        user2 = User.createUser(Email.from(ANNE.getEmail()), ANNE.getLoginId(), Password.encrypt(ANNE.getPassword(), ENCODER), ANNE.getName(), ANNE.getBirth(), ANNE.getPhoneNumber(), ANNE.getRole());
    }

    @Test
    @DisplayName("PartTimer를 추가한다")
    void addPartTimer() {
        // given
        PartTimers partTimers = PartTimers.createPartTimers();

        // when
        partTimers.addPartTimer(PartTimer.createPartTimer(store, user1));
        partTimers.addPartTimer(PartTimer.createPartTimer(store, user2));

        // then
        assertAll(
                () -> assertThat(partTimers.getPartTimers()).hasSize(2),
                () -> assertThat(partTimers.getPartTimers())
                        .map(PartTimer::getPartTimer)
                        .contains(user1, user2)
        );
    }
}