package umc.stockoneqback.auth.service.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
