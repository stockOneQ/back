package umc.stockoneqback.friend.infra.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.domain.QFriend;
import umc.stockoneqback.friend.infra.query.dto.response.FriendInformation;
import umc.stockoneqback.friend.infra.query.dto.response.QFriendInformation;

import java.util.List;

import static umc.stockoneqback.role.domain.store.QStore.store;
import static umc.stockoneqback.user.domain.QUser.user;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendInformationQueryRepositoryImpl implements FriendInformationQueryRepository {
    private final JPAQueryFactory query;
    private static final QFriend friend = new QFriend("friend");

    @Override
    public List<FriendInformation> findReceiversByUserIdAndFriendStatus(Long userId, FriendStatus friendStatus) {
        return query
                .selectDistinct(new QFriendInformation(friend.receiver.id, friend.receiver.name, friend.receiver.managerStore.name, friend.receiver.phoneNumber, friend.friendStatus, friend.modifiedDate))
                .from(friend)
                .innerJoin(user).on(friend.receiver.id.eq(user.id))
                .innerJoin(store).on(friend.receiver.managerStore.id.eq(store.id))
                .where(friend.sender.id.eq(userId), friend.friendStatus.eq(friendStatus))
                .orderBy(friend.modifiedDate.desc())
                .fetch();
    }

    @Override
    public List<FriendInformation> findSendersByUserIdAndFriendStatus(Long userId, FriendStatus friendStatus) {
        return query
                .selectDistinct(new QFriendInformation(friend.sender.id, friend.sender.name, friend.sender.managerStore.name, friend.sender.phoneNumber, friend.friendStatus, friend.modifiedDate))
                .from(friend)
                .innerJoin(user).on(friend.sender.id.eq(user.id))
                .innerJoin(store).on(friend.sender.managerStore.id.eq(store.id))
                .where(friend.receiver.id.eq(userId), friend.friendStatus.eq(friendStatus))
                .orderBy(friend.modifiedDate.desc())
                .fetch();
    }
}
