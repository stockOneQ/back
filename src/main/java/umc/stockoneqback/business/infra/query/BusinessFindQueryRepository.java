package umc.stockoneqback.business.infra.query;

import umc.stockoneqback.business.infra.query.dto.FilteredBusinessUser;

public interface BusinessFindQueryRepository {
    FilteredBusinessUser findBusinessByManager(Long managerId);
    FilteredBusinessUser findBusinessByPartTimer(Long partTimerId);
    FilteredBusinessUser findBusinessBySupervisor(Long supervisorId);
    Long findBusinessIdByPartTimerIdAndSupervisorId(Long partTimerId, Long supervisorId);
}
