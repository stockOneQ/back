package umc.stockoneqback.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.user.infra.query.UserFindQueryRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserFindQueryRepository {
    boolean existsByLoginIdAndStatus(String loginId, Status status);
    boolean existsByEmailAndStatus(Email email, Status status);
    Optional<User> findByLoginIdAndStatus(String loginId, Status status);
    Optional<User> findByEmailAndStatus(Email email, Status status);
    Optional<User> findByIdAndStatus(Long id, Status status);
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE users SET status = '소멸' WHERE id = :userId", nativeQuery = true)
    void expireById(@Param("userId") Long userId);
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "DELETE FROM users WHERE modified_date <= :overYear and status = '소멸'", nativeQuery = true)
    void deleteModifiedOverYearAndExpireUser(@Param("overYear") LocalDate overYear);
    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE users SET modified_date = :modifiedDate WHERE id = :userId", nativeQuery = true)
    void updateModifiedDateById(@Param("userId") Long userId, @Param("modifiedDate") LocalDate modifiedDate);
}
