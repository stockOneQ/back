package umc.stockoneqback.business.infra.query;

import umc.stockoneqback.business.infra.query.dto.FilteredBusinessUser;
import umc.stockoneqback.business.infra.query.dto.FindBusinessUser;

public interface BusinessFindQueryRepository {
    FilteredBusinessUser<FindBusinessUser> findBusinessByManager(Long managerId);
    FilteredBusinessUser<FindBusinessUser> findBusinessByPartTimer(Long partTimerId);
    FilteredBusinessUser<FindBusinessUser> findBusinessBySupervisor(Long supervisorId);
}
