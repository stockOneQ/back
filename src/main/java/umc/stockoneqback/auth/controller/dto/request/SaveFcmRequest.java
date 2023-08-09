package umc.stockoneqback.auth.controller.dto.request;

import javax.validation.constraints.NotBlank;

public record SaveFcmRequest(
        @NotBlank(message = "토큰은 필수입니다.")
        String token
) {
}
