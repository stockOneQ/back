package umc.stockoneqback.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.stockoneqback.user.infra.query.UserFindQueryRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserFindQueryRepository {
    boolean existsByLoginId(String loginId);

    @Query(value = "SELECT * FROM Users u WHERE u.role = 'ROLE_MANAGER' AND u.name LIKE %:search% " +
            "ORDER BY u.name", nativeQuery = true)
    List<User> searchUserByName(@Param("search") String search);

    Optional<User> findByLoginId(String loginId);
}
