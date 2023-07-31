package umc.stockoneqback.user.infra.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.infra.query.dto.FindManager;
import umc.stockoneqback.user.infra.query.dto.QFindManager;

import java.util.List;

import static umc.stockoneqback.role.domain.store.QStore.store;
import static umc.stockoneqback.user.domain.QUser.user;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFindQueryRepositoryImpl implements UserFindQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<FindManager> findManagersByName(String name) {
        return query
                .selectDistinct(new QFindManager(user.id, user.name, user.managerStore.name, user.phoneNumber))
                .from(user)
                .innerJoin(store).on(user.managerStore.id.eq(store.id))
                .where(user.name.contains(name), user.role.eq(Role.MANAGER))
                .orderBy(user.id.asc())
                .fetch();
    }

    @Override
    public List<FindManager> findManagersByStoreName(String storeName) {
        return query
                .selectDistinct(new QFindManager(user.id, user.name, user.managerStore.name, user.phoneNumber))
                .from(user)
                .innerJoin(store).on(user.managerStore.id.eq(store.id))
                .where(store.name.contains(storeName), user.role.eq(Role.MANAGER))
                .orderBy(user.id.asc())
                .fetch();
    }

    @Override
    public List<FindManager> findManagersByAddress(String address) {
        return query
                .selectDistinct(new QFindManager(user.id, user.name, user.managerStore.name, user.phoneNumber))
                .from(user)
                .innerJoin(store).on(user.managerStore.id.eq(store.id))
                .where(store.address.contains(address), user.role.eq(Role.MANAGER))
                .orderBy(user.id.asc())
                .fetch();
    }

    public List<FindManager> findUserByUserId(Long id) {
        return query
                .selectDistinct(new QFindManager(user.id, user.name, user.managerStore.name, user.phoneNumber))
                .from(user)
                .innerJoin(store).on(user.managerStore.id.eq(store.id))
                .where(user.id.eq(id))
                .fetch();
    }
}
