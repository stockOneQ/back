package umc.stockoneqback.user.service.dto.response;

import java.time.LocalDate;

public record UserInformationResponse(
        Long id,
        String email,
        String loginId,
        String name,
        LocalDate birth,
        String phoneNumber,
        String role,
        String storeName,
        String storeCode,
        String storeAddress,
        String companyName
) {
}
