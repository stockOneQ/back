package umc.stockoneqback.auth.service.dto.response;

public record LoginResponse(
        Long userId,
        String loginId,
        String accessToken,
        String refreshToken
) {
}
