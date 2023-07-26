package umc.stockoneqback.friend.service.dto;

import lombok.Builder;
import umc.stockoneqback.role.domain.company.Company;

@Builder
public record SearchUserResponse (
        String name,
        String phoneNumber,
        Company company
) {
}
