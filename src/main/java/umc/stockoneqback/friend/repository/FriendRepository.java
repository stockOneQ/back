package umc.stockoneqback.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.friend.domain.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
