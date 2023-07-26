package umc.stockoneqback.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // @Query
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Token t" +
            " SET t.refreshToken = :refreshToken" +
            " WHERE t.userId = :userId")
    void reissueRefreshTokenByRtrPolicy(@Param("userId") Long userId, @Param("refreshToken") String newRefreshToken);

    // Query Method
    Optional<Token> findByUserId(Long userId);
    boolean existsByUserIdAndRefreshToken(Long userId, String refreshToken);
    void deleteByUserId(Long userId);
}
