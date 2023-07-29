package umc.stockoneqback.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.infra.query.FriendInformationQueryRepository;
import umc.stockoneqback.user.domain.User;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendInformationQueryRepository {
    Optional<Friend> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    boolean existsBySenderAndReceiver(User sender, User receiver);
}
