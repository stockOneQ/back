package umc.stockoneqback.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // @Query
    @Query(value = "SELECT * FROM users u WHERE u.role = 'ROLE_MANAGER' AND u.name LIKE %:search% " +
            "ORDER BY u.name", nativeQuery = true)
    List<User> searchUserByName(@Param("search") String search);

    // query method
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(Email email);
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(Email email);
}
