package umc.stockoneqback.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.user.domain.User;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
