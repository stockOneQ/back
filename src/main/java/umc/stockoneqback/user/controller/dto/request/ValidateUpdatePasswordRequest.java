package umc.stockoneqback.user.controller.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record ValidateUpdatePasswordRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "생일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birth,

        @NotBlank(message = "로그인 아이디는 필수입니다.")
        String loginId
) {
}
