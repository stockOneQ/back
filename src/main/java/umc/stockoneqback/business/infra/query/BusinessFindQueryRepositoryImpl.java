package umc.stockoneqback.business.infra.query;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.infra.query.dto.FilteredBusinessUser;
import umc.stockoneqback.business.infra.query.dto.FindBusinessUser;
import umc.stockoneqback.business.infra.query.dto.QFindBusinessUser;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.role.domain.store.QPartTimer;

import java.util.List;

import static umc.stockoneqback.business.domain.QBusiness.business;
import static umc.stockoneqback.role.domain.store.QStore.store;
import static umc.stockoneqback.user.domain.QUser.user;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BusinessFindQueryRepositoryImpl implements BusinessFindQueryRepository {
    private final JPAQueryFactory query;
    private static final QPartTimer partTimer = new QPartTimer("partTimer");

    @Override
    public FilteredBusinessUser<FindBusinessUser> findBusinessByManager(Long managerId) {
        List<FindBusinessUser> shareLists = query
                .selectDistinct(new QFindBusinessUser(business.supervisor.id, business.supervisor.name))
                .from(business)
                .innerJoin(user).on(user.id.eq(business.supervisor.id))
                .where(business.status.eq(Status.NORMAL),
                        business.manager.id.eq(managerId))
                .orderBy(business.id.asc())
                .distinct()
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(business.countDistinct())
                .from(business)
                .innerJoin(user).on(user.id.eq(business.supervisor.id))
                .where(business.status.eq(Status.NORMAL),
                        business.manager.id.eq(managerId))
                ;

        return new FilteredBusinessUser<>(countQuery.fetchOne(), shareLists);
    }

    @Override
    public FilteredBusinessUser<FindBusinessUser> findBusinessByPartTimer(Long partTimerId) {
        List<FindBusinessUser> shareLists = query
                .selectDistinct(new QFindBusinessUser(business.supervisor.id, business.supervisor.name))
                .from(partTimer)
                .innerJoin(store).on(store.id.eq(partTimer.store.id))
                .innerJoin(business).on(business.manager.id.eq(store.manager.id))
                .innerJoin(user).on(user.id.eq(business.supervisor.id))
                .where(business.status.eq(Status.NORMAL),
                        partTimer.id.eq(partTimerId))
                .orderBy(business.id.asc())
                .distinct()
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(business.countDistinct())
                .from(partTimer)
                .innerJoin(store).on(store.id.eq(partTimer.store.id))
                .innerJoin(business).on(business.manager.id.eq(store.manager.id))
                .innerJoin(user).on(user.id.eq(business.supervisor.id))
                .where(business.status.eq(Status.NORMAL),
                        partTimer.id.eq(partTimerId))
                ;

        return new FilteredBusinessUser<>(countQuery.fetchOne(), shareLists);
    }

    @Override
    public FilteredBusinessUser<FindBusinessUser> findBusinessBySupervisor(Long supervisorId) {
        List<FindBusinessUser> shareLists = query
                .selectDistinct(new QFindBusinessUser(business.manager.id, business.manager.name))
                .from(business)
                .innerJoin(user).on(user.id.eq(business.manager.id))
                .where(business.status.eq(Status.NORMAL),
                        business.supervisor.id.eq(supervisorId))
                .orderBy(business.id.asc())
                .distinct()
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(business.countDistinct())
                .from(business)
                .innerJoin(user).on(user.id.eq(business.manager.id))
                .where(business.status.eq(Status.NORMAL),
                        business.supervisor.id.eq(supervisorId))
                .orderBy(business.id.asc())
                ;

        return new FilteredBusinessUser<>(countQuery.fetchOne(), shareLists);
    }

    public Long findBusinessIdByPartTimerIdAndSupervisorId(Long partTimerId, Long supervisorId) {
        return query
                .selectDistinct(business.id)
                .from(partTimer)
                .innerJoin(store).on(store.id.eq(partTimer.store.id))
                .innerJoin(business).on(business.manager.id.eq(store.manager.id))
                .where(business.status.eq(Status.NORMAL),
                        partTimer.partTimer.id.eq(partTimerId),
                        business.supervisor.id.eq(supervisorId))
                .distinct()
                .fetchOne();
    }
}
