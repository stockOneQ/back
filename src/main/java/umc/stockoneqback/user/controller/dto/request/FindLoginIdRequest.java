package umc.stockoneqback.user.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record FindLoginIdRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "생일은 필수입니다.")
        LocalDate birth,

        @NotBlank(message = "이메일은 필수입니다.")
        String email
) {
}
