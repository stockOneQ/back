package umc.stockoneqback.user.infra.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.search.UserSearchType;
import umc.stockoneqback.user.infra.query.dto.FindManager;
import umc.stockoneqback.user.infra.query.dto.QFindManager;

import java.util.List;

import static umc.stockoneqback.user.domain.QUser.user;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFindQueryRepositoryImpl implements UserFindQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<FindManager> findManagersBySearchType(Long userId, UserSearchType userSearchType, String searchWord) {
        return query
                .selectDistinct(new QFindManager(user.id, user.name, user.managerStore.name, user.phoneNumber))
                .from(user)
                .where(search(userSearchType, searchWord), user.role.eq(Role.MANAGER), user.status.eq(Status.NORMAL), user.id.ne(userId))
                .orderBy(user.id.asc())
                .fetch();
    }

    private BooleanExpression search(UserSearchType userSearchType, String searchWord) {
        return switch (userSearchType) {
            case NAME -> user.name.contains(searchWord);
            case STORE -> user.managerStore.name.contains(searchWord);
            case ADDRESS -> user.managerStore.address.contains(searchWord);
        };
    }
}
