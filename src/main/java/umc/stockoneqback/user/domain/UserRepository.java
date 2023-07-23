package umc.stockoneqback.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);

    @Query(value = "SELECT u.* FROM User u WHERE u.role = 'MANAGER' AND u.name LIKE %:name% ORDER BY u.name", nativeQuery = true)
    List<User> searchUserByName(@Param("name") String searchName);
}
