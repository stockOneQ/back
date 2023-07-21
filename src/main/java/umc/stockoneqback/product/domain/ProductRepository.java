package umc.stockoneqback.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.stockoneqback.product.dto.response.SearchProductUrl;
import umc.stockoneqback.role.domain.store.Store;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p.id, p.name, p.imageUrl FROM Product p WHERE p.status = 'NORMAL' AND p.store = :store" +
            "AND p.storeCondition = :storeCondition AND p.name LIKE %:name% ORDER BY p.name")
    List<SearchProductUrl> findProductByName(@Param("store") Store store,
                                             @Param("storeCondition") StoreCondition storeCondition,
                                             @Param("name") String productName);
    @Query("SELECT 1 FROM Product p WHERE p.status = 'NORMAL' AND p.store = :store" +
            "AND p.storeCondition = :storeCondition AND p.name = :name LIMIT 1")
    Integer isExistProductByName(@Param("store") Store store,
                                 @Param("storeCondition") StoreCondition storeCondition,
                                 @Param("name") String productName);

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.status = 'NORMAL'")
    Optional<Product> findProductById(@Param("id") Long productId);

    @Query("SELECT count(*) FROM Product p WHERE p.store = :store AND p.storeCondition = :storeCondition AND p.status = 'NORMAL'")
    Integer countProductAll(@Param("store") Store store,
                            @Param("storeCondition") StoreCondition storeCondition);
    @Query("SELECT count(*) FROM Product p WHERE p.store = :store AND p.storeCondition = :storeCondition" +
            "AND p.expirationDate < :currentDate AND p.status = 'NORMAL'")
    Integer countProductPass(@Param("store") Store store,
                             @Param("storeCondition") StoreCondition storeCondition,
                             @Param("currentDate") LocalDate currentDate);
    @Query("SELECT count(*) FROM Product p WHERE p.store = :store AND p.storeCondition = :storeCondition" +
            "AND (p.expirationDate >= :currentDate AND p.expirationDate <= :standardDate) AND p.status = 'NORMAL'")
    Integer countProductClose(@Param("store") Store store,
                              @Param("storeCondition") StoreCondition storeCondition,
                              @Param("currentDate") LocalDate currentDate,
                              @Param("standardDate") LocalDate standardDate);
    @Query("SELECT count(*) FROM Product p WHERE p.store = :store AND p.storeCondition = :storeCondition" +
            "AND p.requireQuant > p.stockQuant AND p.status = 'NORMAL'")
    Integer countProductLack(@Param("store") Store store,
                             @Param("storeCondition") StoreCondition storeCondition);
}
