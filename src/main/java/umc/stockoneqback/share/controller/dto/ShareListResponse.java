package umc.stockoneqback.share.controller.dto;

import umc.stockoneqback.business.infra.query.dto.FilteredBusinessUser;
import umc.stockoneqback.business.infra.query.dto.FindBusinessUser;
import umc.stockoneqback.share.infra.query.dto.CustomShareListPage;
import umc.stockoneqback.share.infra.query.dto.ShareList;

public record ShareListResponse(
        FilteredBusinessUser<FindBusinessUser> filterBusinessUserList,
        CustomShareListPage<ShareList> shareListPage
) {
}