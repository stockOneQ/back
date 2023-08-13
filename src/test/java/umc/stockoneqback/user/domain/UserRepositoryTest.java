package umc.stockoneqback.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.global.base.Status;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.*;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("User [Repository Test] -> UserRepository 테스트")
class UserRepositoryTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User saewoo;
    private User anne;

    @BeforeEach
    void setUp() {
        saewoo = userRepository.save(SAEWOO.toUser());
        anne = userRepository.save(ANNE.toUser());
        userRepository.save(WIZ.toUser());
        userRepository.save(WONI.toUser());
    }

    @Test
    @DisplayName("로그인 아이디로 사용자가 존재하는지 확인한다")
    void existsByLoginId() {
        // when
        boolean actual1 = userRepository.existsByLoginIdAndStatus(SAEWOO.getLoginId(), Status.NORMAL);
        boolean actual2 = userRepository.existsByLoginIdAndStatus(ANNE.getLoginId(), Status.NORMAL);
        boolean actual3 = userRepository.existsByLoginIdAndStatus(WIZ.getLoginId(), Status.NORMAL);
        boolean actual4 = userRepository.existsByLoginIdAndStatus(WONI.getLoginId(), Status.NORMAL);
        boolean actual5 = userRepository.existsByLoginIdAndStatus(WONI.getLoginId() + "fake", Status.NORMAL);
        boolean actual6 = userRepository.existsByLoginIdAndStatus(ANNE.getLoginId() + "fake", Status.NORMAL);
        boolean actual7 = userRepository.existsByLoginIdAndStatus(WIZ.getLoginId() + "fake", Status.NORMAL);
        boolean actual8 = userRepository.existsByLoginIdAndStatus(WONI.getLoginId() + "fake", Status.NORMAL);

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
        boolean actual1 = userRepository.existsByEmailAndStatus(Email.from(SAEWOO.getEmail()), Status.NORMAL);
        boolean actual2 = userRepository.existsByEmailAndStatus(Email.from(ANNE.getEmail()), Status.NORMAL);
        boolean actual3 = userRepository.existsByEmailAndStatus(Email.from(WIZ.getEmail()), Status.NORMAL);
        boolean actual4 = userRepository.existsByEmailAndStatus(Email.from(WONI.getEmail()), Status.NORMAL);
        boolean actual5 = userRepository.existsByEmailAndStatus(Email.from("fakefake@gmail.com"), Status.NORMAL);

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
        User findUser = userRepository.findByLoginIdAndStatus(SAEWOO.getLoginId(), Status.NORMAL).orElseThrow();

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
    @DisplayName("이메일로 사용자를 조회한다")
    void findByEmail() {
        // when
        User findUser = userRepository.findByEmailAndStatus(Email.from(SAEWOO.getEmail()), Status.NORMAL).orElseThrow();

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
    @DisplayName("고유 ID로 사용자를 조회한다")
    void findById() {
        // when
        User findUser = userRepository.findByIdAndStatus(saewoo.getId(), Status.NORMAL).orElseThrow();

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
    @DisplayName("사용자를 소멸 상태로 변경한다")
    void expireById() {
        // when
        userRepository.expireById(saewoo.getId());

        // then
        Optional<User> user = userRepository.findByLoginIdAndStatus(saewoo.getLoginId(), Status.NORMAL);
        assertThat(user.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("1년이 지난 소멸 상태의 사용자를 모두 삭제한다")
    void deleteModifiedOverYearAndExpireUser() {
        // given
        userRepository.expireById(saewoo.getId());
        userRepository.expireById(anne.getId());

        LocalDate overTwoYear = LocalDate.now().minusYears(2);
        userRepository.updateModifiedDateById(saewoo.getId(), overTwoYear);
        userRepository.updateModifiedDateById(anne.getId(), overTwoYear);

        // when
        LocalDate overYear = LocalDate.now().minusYears(1);
        userRepository.deleteModifiedOverYearAndExpireUser(overYear);

        // then
        Optional<User> findSaewoo = userRepository.findById(saewoo.getId());
        Optional<User> findAnne = userRepository.findById(anne.getId());
        assertAll(
                () -> assertThat(findSaewoo.isEmpty()).isTrue(),
                () -> assertThat(findAnne.isEmpty()).isTrue()
        );
    }
}