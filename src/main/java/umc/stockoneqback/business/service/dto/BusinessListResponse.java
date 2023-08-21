package umc.stockoneqback.business.service.dto;

import umc.stockoneqback.business.infra.query.dto.BusinessList;

import java.util.List;

public record BusinessListResponse(
        List<BusinessList> userList
) {
}
