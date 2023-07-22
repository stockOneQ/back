package umc.stockoneqback.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.stockoneqback.product.dto.response.SearchProductUrl;
import umc.stockoneqback.role.domain.store.Store;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    /*
    @Query("SELECT p.name, p.imageUrl FROM Product p WHERE p.status = 'NORMAL' AND p.store = :store" +
            "AND p.storeCondition = :storeCondition AND p.name LIKE %:name% ORDER BY p.name")
    List<SearchProductUrl> findProductByName(@Param("store") Store store,
                                             @Param("storeCondition") StoreCondition storeCondition,
                                             @Param("name") String productName);
    @Query("SELECT 1 FROM Product p WHERE p.status = 'NORMAL' AND p.store = :store" +
            "AND p.storeCondition = :storeCondition AND p.name = :name LIMIT 1")
    Integer isExistProductByName(@Param("store") Store store,
                                 @Param("storeCondition") StoreCondition storeCondition,
                                 @Param("name") String productName);

    @Query("SELECT p FROM Product p WHERE p.status = 'NORMAL' AND p.store = :store" +
            "AND p.storeCondition = :storeCondition AND p.id = :id")
    Optional<Product> findProductById(@Param("store") Store store,
                                      @Param("storeCondition") StoreCondition storeCondition,
                                      @Param("id") Long productId);
    */
}


