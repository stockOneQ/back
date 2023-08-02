package umc.stockoneqback.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.global.base.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.*;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

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
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isTrue(),
                () -> assertThat(actual5).isFalse(),
                () -> assertThat(actual6).isFalse(),
                () -> assertThat(actual7).isFalse(),
                () -> assertThat(actual8).isFalse()
        );
    }

    @Test
    @DisplayName("이메일로 사용자가 존재하는지 확인한다")
    void existsByEmail() {
        // when
        boolean actual1 = userRepository.existsByEmail(Email.from(SAEWOO.getEmail()));
        boolean actual2 = userRepository.existsByEmail(Email.from(ANNE.getEmail()));
        boolean actual3 = userRepository.existsByEmail(Email.from(WIZ.getEmail()));
        boolean actual4 = userRepository.existsByEmail(Email.from(WONI.getEmail()));
        boolean actual5 = userRepository.existsByEmail(Email.from("fakefake@gmail.com"));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isTrue(),
                () -> assertThat(actual5).isFalse()
        );
    }

    @Test
    @DisplayName("로그인 아이디로 사용자를 조회한다")
    void findByLoginId() {
        // when
        User findUser = userRepository.findByLoginId(SAEWOO.getLoginId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findUser.getEmail().isSameEmail(Email.from(SAEWOO.getEmail()))).isTrue(),
                () -> assertThat(findUser.getLoginId()).isEqualTo(SAEWOO.getLoginId()),
                () -> assertThat(findUser.getPassword().isSamePassword(SAEWOO.getPassword(), ENCODER)).isTrue(),
                () -> assertThat(findUser.getName()).isEqualTo(SAEWOO.getName()),
                () -> assertThat(findUser.getPhoneNumber()).isEqualTo(SAEWOO.getPhoneNumber()),
                () -> assertThat(findUser.getRole()).isEqualTo(SAEWOO.getRole()),
                () -> assertThat(findUser.getStatus()).isEqualTo(Status.NORMAL)
        );
    }

    @Test
    @DisplayName("이메일를 조회한다")
    void findByEmail() {
        // when
        User findUser = userRepository.findByEmail(Email.from(SAEWOO.getEmail())).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findUser.getEmail().isSameEmail(Email.from(SAEWOO.getEmail()))).isTrue(),
                () -> assertThat(findUser.getLoginId()).isEqualTo(SAEWOO.getLoginId()),
                () -> assertThat(findUser.getPassword().isSamePassword(SAEWOO.getPassword(), ENCODER)).isTrue(),
                () -> assertThat(findUser.getName()).isEqualTo(SAEWOO.getName()),
                () -> assertThat(findUser.getPhoneNumber()).isEqualTo(SAEWOO.getPhoneNumber()),
                () -> assertThat(findUser.getRole()).isEqualTo(SAEWOO.getRole()),
                () -> assertThat(findUser.getStatus()).isEqualTo(Status.NORMAL)
        );
    }
}