package umc.stockoneqback.auth.controller.dto.request;

import javax.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "로그인 아이디는 필수입니다.")
        String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
