package umc.stockoneqback.business.infra.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.infra.query.dto.BusinessList;
import umc.stockoneqback.business.infra.query.dto.QBusinessList;
import umc.stockoneqback.global.base.RelationStatus;

import java.util.List;

import static umc.stockoneqback.business.domain.QBusiness.business;
import static umc.stockoneqback.user.domain.QUser.user;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BusinessListQueryRepositoryImpl implements BusinessListQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<BusinessList> findSupervisorByManagerIdAndRelationStatus(Long managerId, RelationStatus relationStatus, String search) {
        return query
                .selectDistinct(new QBusinessList(
                        business.supervisor.id,
                        business.supervisor.name,
                        business.supervisor.company.name,
                        business.supervisor.phoneNumber,
                        business.modifiedDate))
                .from(business)
                .innerJoin(user).on(business.supervisor.id.eq(user.id))
                .where(business.manager.id.eq(managerId), business.relationStatus.eq(relationStatus), searchSupervisor(search))
                .orderBy(business.modifiedDate.desc())
                .fetch();
    }

    @Override
    public List<BusinessList> findManagerBySupervisorIdAndRelationStatus(Long supervisorId, RelationStatus relationStatus) {
        return query
                .selectDistinct(new QBusinessList(
                        business.manager.id,
                        business.manager.name,
                        business.manager.managerStore.name,
                        business.manager.phoneNumber,
                        business.modifiedDate))
                .from(business)
                .innerJoin(user).on(business.manager.id.eq(user.id))
                .where(business.supervisor.id.eq(supervisorId), business.relationStatus.eq(relationStatus))
                .orderBy(business.modifiedDate.desc())
                .fetch();
    }

    private BooleanExpression searchSupervisor(String searchWord) {
        if (searchWord == null || searchWord.isEmpty()) {
            return null;
        } else {
            return business.supervisor.name.contains(searchWord);
        }
    }
}
