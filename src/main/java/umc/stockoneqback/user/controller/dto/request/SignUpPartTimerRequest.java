package umc.stockoneqback.user.controller.dto.request;

import org.springframework.format.annotation.DateTimeFormat;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

public record SignUpPartTimerRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "생일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birth,

        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "전화번호는 필수입니다.")
        String phoneNumber,

        @NotBlank(message = "아이디는 필수입니다.")
        String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @NotBlank(message = "가게 이름은 필수입니다.")
        String storeName,

        @NotBlank(message = "가게 코드는 필수입니다.")
        String storeCode
) {
        public User toUser() {
                return User.createUser(
                        Email.from(email),
                        loginId,
                        Password.encrypt(password, ENCODER),
                        name,
                        birth,
                        phoneNumber,
                        Role.PART_TIMER
                );
        }
}
