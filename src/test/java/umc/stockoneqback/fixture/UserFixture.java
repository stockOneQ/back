package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;

import java.time.LocalDate;

import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@Getter
@RequiredArgsConstructor
public enum UserFixture {
    SAEWOO("saewoo@naver.com", "NewPass-2023", "새우", LocalDate.of(2001, 4, 22), "01012345678", "아르바이트생"),
    ANNE("anne@gmail.com", "Strong123!", "앤", LocalDate.of(1995, 5, 12), "01098765432", "매니저"),
    WIZ("wiz@yahoo.com", "Secure456!", "위즈", LocalDate.of(1988, 9, 25), "01056781234", "슈퍼바이저"),
    WONI("woni@hanmail.com", "Pass1234$", "워니", LocalDate.of(2000, 12, 31), "01024681357", "아르바이트생")
    ;

    private final String email;
    private final String password;
    private final String username;
    private final LocalDate birth;
    private final String phoneNumber;
    private final String role;

    public User toUser() {
        return User.createUser(Email.from(email), Password.encrypt(password, ENCODER), username, birth, phoneNumber, role);
    }
}
