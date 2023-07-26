package umc.stockoneqback.user.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);

    @Query(value = "SELECT * FROM Users u WHERE u.role = :role AND u.name LIKE %:search% ORDER BY u.name", nativeQuery = true)
    Page searchUserByName(@Param("role") Role role, @Param("search") String search, Pageable pageable);

    Optional<User> findByLoginId(String loginId);
}
