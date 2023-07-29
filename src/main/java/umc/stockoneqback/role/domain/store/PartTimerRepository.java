package umc.stockoneqback.role.domain.store;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.user.domain.User;

import java.util.Optional;

public interface PartTimerRepository extends JpaRepository<PartTimer, Long> {
    Optional<PartTimer> findByPartTimer(User user);
}
