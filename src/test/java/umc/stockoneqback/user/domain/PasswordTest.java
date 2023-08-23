package umc.stockoneqback.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.exception.UserErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("Password VO 테스트")
public class PasswordTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"password", "12345678!", "123456789101121314", "Abcd", "a|b|c|d|1|2"})
    @DisplayName("비밀번호 패턴에 맞지 않아 비밀번호 생성에 실패한다")
    void throwExceptionByInvalidNicknamePattern(String value) {
        assertThatThrownBy(() -> Password.encrypt(value, ENCODER))
                .isInstanceOf(BaseException.class)
                .hasMessage(UserErrorCode.INVALID_PASSWORD_PATTERN.getMessage());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"12DefGh!@#", "P@ssWord!38", "Secure@2589", "NewPass-202!", "5!Passw0rd@"})
    @DisplayName("비밀번호 생성에 성공한다")
    void createPassword(String value) {
        Password password = Password.encrypt(value, ENCODER);

        assertThat(password.isSamePassword(value, ENCODER)).isTrue();
    }
}
