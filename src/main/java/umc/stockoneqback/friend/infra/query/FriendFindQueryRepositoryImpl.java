package umc.stockoneqback.friend.infra.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.user.service.dto.FindManager;
import umc.stockoneqback.user.service.dto.QFindManager;

import java.util.List;

import static umc.stockoneqback.role.domain.store.QStore.store;
import static umc.stockoneqback.user.domain.QUser.user;
import static umc.stockoneqback.friend.domain.QFriend.friend;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendFindQueryRepositoryImpl implements FriendFindQueryRepository{
    private final JPAQueryFactory query;

    @Override
    public List<FindManager> findReceiversByUserIdAndName(Long userId, String name, FriendStatus friendStatus) {
        return query
                .selectDistinct(new QFindManager(friend.receiver.id, friend.receiver.name, friend.receiver.managerStore.name, friend.receiver.phoneNumber))
                .from(friend)
                .innerJoin(user).on(friend.receiver.id.eq(user.id))
                .innerJoin(store).on(friend.receiver.managerStore.id.eq(store.id))
                .where(friend.sender.id.eq(userId), friend.receiver.name.contains(name))
                .orderBy(friend.receiver.managerStore.id.desc())
                .fetch();
    }
}
