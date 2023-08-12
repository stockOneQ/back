package umc.stockoneqback.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("User [Service Layer] -> UserWithdrawService 테스트")
public class UserWithdrawServiceTest extends ServiceTest {
    @Autowired
    private UserWithdrawService userWithdrawService;

    @Autowired
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(SAEWOO.toUser());
        authService.login(SAEWOO.getLoginId(), SAEWOO.getPassword());
    }

    @Test
    @DisplayName("사용자 관련 정보를 삭제하고 사용자 개인정보의 상태를 변경한다")
    void withdrawUser() {
        // when
        userWithdrawService.withdrawUser(user.getId());

        // then
        User expiredUser = userRepository.findById(user.getId()).orElseThrow();
        assertAll(
                () -> assertThat(expiredUser.getStatus()).isEqualTo(Status.EXPIRED),
                () -> assertThat(tokenRepository.findByUserId(user.getId()).isEmpty()).isTrue()
        );
    }
}
