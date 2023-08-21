package umc.stockoneqback.business.infra.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.infra.query.dto.BusinessList;
import umc.stockoneqback.business.infra.query.dto.QBusinessList;
import umc.stockoneqback.global.base.RelationStatus;

import java.util.List;

import static umc.stockoneqback.business.domain.QBusiness.business;
import static umc.stockoneqback.role.domain.company.QCompany.company;
import static umc.stockoneqback.role.domain.store.QStore.store;
import static umc.stockoneqback.user.domain.QUser.user;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BusinessListQueryRepositoryImpl implements BusinessListQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<BusinessList> findSupervisorByManagerIdAndRelationStatus(Long managerId, RelationStatus relationStatus) {
        return query
                .selectDistinct(new QBusinessList(
                        business.supervisor.id,
                        business.supervisor.name,
                        business.supervisor.company.name,
                        business.supervisor.phoneNumber,
                        business.relationStatus,
                        business.modifiedDate))
                .from(business)
                .innerJoin(user).on(business.supervisor.id.eq(user.id))
                .where(business.manager.id.eq(managerId), business.relationStatus.eq(relationStatus))
                .orderBy(business.modifiedDate.desc())
                .fetch();
    }

    public List<BusinessList> findSupervisorByManagerIdAndRelationStatusf(Long managerId, RelationStatus relationStatus) {
        return query
                .selectDistinct(new QBusinessList(
                        business.id,
                        business.supervisor.name,
                        business.supervisor.managerStore.name,
                        business.supervisor.phoneNumber,
                        business.relationStatus,
                        business.modifiedDate))
                .from(business)
                .innerJoin(user).on(business.supervisor.id.eq(user.id))
                .where(business.manager.id.eq(managerId), business.relationStatus.eq(relationStatus))
                .orderBy(business.modifiedDate.desc())
                .fetch();
    }
}