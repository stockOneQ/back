package umc.stockoneqback.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.user.infra.query.UserFindQueryRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserFindQueryRepository {
    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);
}
