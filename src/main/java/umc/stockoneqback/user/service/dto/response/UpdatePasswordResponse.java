package umc.stockoneqback.user.service.dto.response;

public record UpdatePasswordResponse(
        String loginId,
        Boolean validate
) {
}
