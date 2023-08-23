package umc.stockoneqback.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Email VO 테스트")
public class EmailTest {
    @Test
    @DisplayName("이메일 형식이 맞는지 확인한다")
    void throwExceptionByInvalidEmailFormat() {
        String[] corrects = {"abc@gmail.com", "ganada123@naver.com", "hihi@kyonggi.ac.kr"};
        String[] wrongs = {"abc@gmail", "@gmail.com", "aaa", "123@123"};

        Arrays.stream(corrects).forEach(value -> assertPass(value));
        Arrays.stream(wrongs).forEach(value -> assertException(value));
    }

    private void assertPass(String value) {
        Email email = Email.from(value);
        assertThat(email.getValue()).isEqualTo(value);
    }

    private static void assertException(String value) {
        assertThatThrownBy(() -> Email.from(value))
                .isInstanceOf(BaseException.class)
                .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @Test
    @DisplayName("동일한 이메일인지 확인한다")
    void isSameEmail(){
        // given
        Email email = Email.from("same@naver.com");
        Email test1 = Email.from("same@naver.com");
        Email test2 = Email.from("diff@naver.com");

        // when
        boolean result1 = email.isSameEmail(test1);
        boolean result2 = email.isSameEmail(test2);

        // then
        assertAll(
                () -> assertThat(result1).isTrue(),
                () -> assertThat(result2).isFalse()
        );
    }
}
