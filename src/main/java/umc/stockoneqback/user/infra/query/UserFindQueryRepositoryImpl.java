package umc.stockoneqback.user.infra.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.search.SearchType;
import umc.stockoneqback.user.infra.query.dto.FindManager;
import umc.stockoneqback.user.infra.query.dto.QFindManager;

import java.util.List;

import static umc.stockoneqback.user.domain.QUser.user;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFindQueryRepositoryImpl implements UserFindQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<FindManager> findManagersBySearchType(SearchType searchType, String searchWord) {
        return query
                .selectDistinct(new QFindManager(user.id, user.name, user.managerStore.name, user.phoneNumber))
                .from(user)
                .where(search(searchType, searchWord), user.role.eq(Role.MANAGER))
                .orderBy(user.id.asc())
                .fetch();
    }

    private BooleanExpression search(SearchType searchType, String searchWord) {
        if (searchWord == null || searchWord.isEmpty()) {
            return null;
        } else {
            switch (searchType) {
                case NAME: return user.name.contains(searchWord);
                case STORE: return user.managerStore.name.contains(searchWord);
                case ADDRESS: return user.managerStore.address.contains(searchWord);
                default: return null;
            }
        }
    }
}
