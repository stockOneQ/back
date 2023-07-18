package umc.stockoneqback.user.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("User [Repository Test] -> UserRepository 테스트")
class UserRepositoryTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(SAEWOO.toUser());
        userRepository.save(ANNE.toUser());
        userRepository.save(WIZ.toUser());
        userRepository.save(WONI.toUser());
    }

    @Test
    @DisplayName("로그인 아이디로 사용자가 존재하는지 확인한다")
    void existsByLoginId() {
        // when
        boolean actual1 = userRepository.existsByLoginId(SAEWOO.getLoginId());
        boolean actual2 = userRepository.existsByLoginId(ANNE.getLoginId());
        boolean actual3 = userRepository.existsByLoginId(WIZ.getLoginId());
        boolean actual4 = userRepository.existsByLoginId(WONI.getLoginId());
        boolean actual5 = userRepository.existsByLoginId(WONI.getLoginId() + "fake");
        boolean actual6 = userRepository.existsByLoginId(ANNE.getLoginId() + "fake");
        boolean actual7 = userRepository.existsByLoginId(WIZ.getLoginId() + "fake");
        boolean actual8 = userRepository.existsByLoginId(WONI.getLoginId() + "fake");
        // then
        assertAll(
                () -> Assertions.assertThat(actual1).isTrue(),
                () -> Assertions.assertThat(actual2).isTrue(),
                () -> Assertions.assertThat(actual3).isTrue(),
                () -> Assertions.assertThat(actual4).isTrue(),
                () -> Assertions.assertThat(actual5).isFalse(),
                () -> Assertions.assertThat(actual6).isFalse(),
                () -> Assertions.assertThat(actual7).isFalse(),
                () -> Assertions.assertThat(actual8).isFalse()
        );
    }
}