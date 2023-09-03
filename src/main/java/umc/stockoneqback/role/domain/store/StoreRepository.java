package umc.stockoneqback.role.domain.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.stockoneqback.user.domain.User;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    // @Query
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE store SET manager_id = :managerId WHERE id = :storeId", nativeQuery = true)
    void updateManagerIdById(@Param("storeId") Long storeId, @Param("managerId") Long managerId);

    // Query Method
    boolean existsByName(String name);
    Optional<Store> findByName(String name);
    Optional<Store> findByManager(User user);
}
