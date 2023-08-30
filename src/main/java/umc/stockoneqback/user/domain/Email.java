package umc.stockoneqback.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.exception.UserErrorCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Email {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";
    private static final Pattern EMAIL_MATCHER = Pattern.compile(EMAIL_PATTERN);

    @Column(name = "email", nullable = false, unique = true, updatable = false)
    private String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email from(String value) {
        validateEmailPattern(value);
        return new Email(value);
    }

    private static void validateEmailPattern(String value) {
        if (isNotValidPattern(value)) {
            throw BaseException.type(UserErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    private static boolean isNotValidPattern(String email) {
        return !EMAIL_MATCHER.matcher(email).matches();
    }

    public boolean isSameEmail(Email email){
        return this.value.equals(email.getValue());
    }
}