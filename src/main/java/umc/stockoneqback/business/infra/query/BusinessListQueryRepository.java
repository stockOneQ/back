package umc.stockoneqback.business.infra.query;

import umc.stockoneqback.business.infra.query.dto.BusinessList;
import umc.stockoneqback.global.base.RelationStatus;

import java.util.List;

public interface BusinessListQueryRepository {
    List<BusinessList> findSupervisorByManagerIdAndRelationStatus(Long managerId, RelationStatus relationStatus, String search);
    List<BusinessList> findManagerBySupervisorIdAndRelationStatus(Long managerId, RelationStatus relationStatus);
}
