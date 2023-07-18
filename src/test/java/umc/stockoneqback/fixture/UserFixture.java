package umc.stockoneqback.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;

import java.time.LocalDate;

import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@Getter
@RequiredArgsConstructor
public enum UserFixture {
    SAEWOO("saewoo@naver.com", "saewoo123", "NewPass-2023", "새우", LocalDate.of(2001, 4, 22), "01012345678", Role.PART_TIMER),
    ANNE("anne@gmail.com", "anne123", "Strong123!", "앤", LocalDate.of(1995, 5, 12), "01098765432", Role.MANAGER),
    WIZ("wiz@yahoo.com", "wiz123", "Secure456!", "위즈", LocalDate.of(1988, 9, 25), "01056781234", Role.SUPERVISOR),
    WONI("woni@hanmail.com", "woni123", "Pass1234$", "워니", LocalDate.of(2000, 12, 31), "01024681357", Role.PART_TIMER)
    ;

    private final String email;
    private final String loginId;
    private final String password;
    private final String name;
    private final LocalDate birth;
    private final String phoneNumber;
    private final Role role;

    public User toUser() {
        return User.createUser(Email.from(email), loginId, Password.encrypt(password, ENCODER), name, birth, phoneNumber, role);
    }
}
