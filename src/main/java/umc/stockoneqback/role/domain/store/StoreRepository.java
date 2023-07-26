package umc.stockoneqback.role.domain.store;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.user.domain.User;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);
    Optional<Store> findByName(String name);
    Optional<Store> findByManager(User user);
}
