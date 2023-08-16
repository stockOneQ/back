package umc.stockoneqback.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.stockoneqback.product.infra.query.FindProductQueryRepository;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, FindProductQueryRepository {
    @Query(value = "SELECT p.* FROM product p WHERE p.status = '정상' AND p.store = :store " +
            "AND p.store_condition = :storeCondition AND p.name = :name", nativeQuery = true)
    Optional<Product> isExistProductByName(@Param("store") Store store,
                                 @Param("storeCondition") String storeCondition,
                                 @Param("name") String productName);

    @Query(value = "SELECT p.* FROM product p WHERE p.id = :id AND p.status = '정상'", nativeQuery = true)
    Optional<Product> findProductById(@Param("id") Long productId);

    @Query(value = "SELECT count(*) FROM product p WHERE p.store = :store " +
            "AND p.store_condition = :storeCondition AND p.status = '정상'", nativeQuery = true)
    Integer countProductAll(@Param("store") Store store,
                            @Param("storeCondition") String storeCondition);
    @Query(value = "SELECT count(*) FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.expiration_date < :currentDate AND p.status = '정상'", nativeQuery = true)
    Integer countProductPass(@Param("store") Store store,
                             @Param("storeCondition") String storeCondition,
                             @Param("currentDate") LocalDate currentDate);
    @Query(value = "SELECT count(*) FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND (p.expiration_date >= :currentDate AND p.expiration_date <= :standardDate) AND p.status = '정상'", nativeQuery = true)
    Integer countProductClose(@Param("store") Store store,
                              @Param("storeCondition") String storeCondition,
                              @Param("currentDate") LocalDate currentDate,
                              @Param("standardDate") LocalDate standardDate);
    @Query(value = "SELECT count(*) FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.require_quant >= p.stock_quant AND p.status = '정상'", nativeQuery = true)
    Integer countProductLack(@Param("store") Store store,
                             @Param("storeCondition") String storeCondition);

    @Query(value = "SELECT p.* FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.status = '정상' AND (p.name > :name OR :name is null) ORDER BY p.name LIMIT :pageSize", nativeQuery = true)
    List<Product> findPageOfAllOrderByName(@Param("store") Store store,
                                                     @Param("storeCondition") String storeCondition,
                                                     @Param("name") String productName,
                                                     @Param("pageSize") Integer pageSize);

    @Query(value = "SELECT p.* FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.status = '정상' AND ((:orderFreq is null AND :name is null) OR " +
            "(p.order_freq < :orderFreq OR p.order_freq = :orderFreq AND p.name > :name)) " +
            "ORDER BY p.order_freq DESC, p.name LIMIT :pageSize", nativeQuery = true)
    List<Product> findPageOfAllOrderByOrderFreq(@Param("store") Store store,
                                                     @Param("storeCondition") String storeCondition,
                                                     @Param("name") String productName,
                                                     @Param("orderFreq") Long orderFreq,
                                                     @Param("pageSize") Integer pageSize);

    @Query(value = "SELECT p.* FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.status = '정상' AND p.expiration_date < :currentDate " +
            "AND (p.name > :name OR :name is null) ORDER BY p.name LIMIT :pageSize", nativeQuery = true)
    List<Product> findPageOfPassOrderByName(@Param("store") Store store,
                                                    @Param("storeCondition") String storeCondition,
                                                    @Param("name") String productName,
                                                    @Param("pageSize") Integer pageSize,
                                                     @Param("currentDate") LocalDate currentDate);

    @Query(value = "SELECT p.* FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.status = '정상' AND p.expiration_date < :currentDate " +
            "AND ((p.order_freq < :orderFreq OR p.order_freq = :orderFreq AND p.name > :name) " +
            "OR (:orderFreq is null AND :name is null)) ORDER BY p.order_freq DESC, p.name LIMIT :pageSize", nativeQuery = true)
    List<Product> findPageOfPassOrderByOrderFreq(@Param("store") Store store,
                                                         @Param("storeCondition") String storeCondition,
                                                         @Param("name") String productName,
                                                         @Param("orderFreq") Long orderFreq,
                                                         @Param("pageSize") Integer pageSize,
                                                          @Param("currentDate") LocalDate currentDate);

    @Query(value = "SELECT p.* FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.status = '정상' AND (p.expiration_date >= :currentDate AND p.expiration_date <= :standardDate) " +
            "AND (p.name > :name OR :name is null) ORDER BY p.name LIMIT :pageSize", nativeQuery = true)
    List<Product> findPageOfCloseOrderByName(@Param("store") Store store,
                                                    @Param("storeCondition") String storeCondition,
                                                    @Param("name") String productName,
                                                    @Param("pageSize") Integer pageSize,
                                                    @Param("currentDate") LocalDate currentDate,
                                                    @Param("standardDate") LocalDate standardDate);

    @Query(value = "SELECT p.* FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.status = '정상' AND (p.expiration_date >= :currentDate AND p.expiration_date <= :standardDate) " +
            "AND ((p.order_freq < :orderFreq OR p.order_freq = :orderFreq AND p.name > :name) " +
            "OR (:orderFreq is null AND :name is null)) ORDER BY p.order_freq DESC, p.name LIMIT :pageSize", nativeQuery = true)
    List<Product> findPageOfCloseOrderByOrderFreq(@Param("store") Store store,
                                                       @Param("storeCondition") String storeCondition,
                                                       @Param("name") String productName,
                                                       @Param("orderFreq") Long orderFreq,
                                                       @Param("pageSize") Integer pageSize,
                                                       @Param("currentDate") LocalDate currentDate,
                                                       @Param("standardDate") LocalDate standardDate);

    @Query(value = "SELECT p.* FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.status = '정상' AND p.require_quant >= p.stock_quant " +
            "AND (p.name > :name OR :name is null) ORDER BY p.name LIMIT :pageSize", nativeQuery = true)
    List<Product> findPageOfLackOrderByName(@Param("store") Store store,
                                                    @Param("storeCondition") String storeCondition,
                                                    @Param("name") String productName,
                                                    @Param("pageSize") Integer pageSize);

    @Query(value = "SELECT p.* FROM product p WHERE p.store = :store AND p.store_condition = :storeCondition " +
            "AND p.status = '정상' AND p.require_quant >= p.stock_quant " +
            "AND ((p.order_freq < :orderFreq OR p.order_freq = :orderFreq AND p.name > :name) " +
            "OR (:orderFreq is null AND :name is null)) ORDER BY p.order_freq DESC, p.name LIMIT :pageSize", nativeQuery = true)
    List<Product> findPageOfLackOrderByOrderFreq(@Param("store") Store store,
                                                         @Param("storeCondition") String storeCondition,
                                                         @Param("name") String productName,
                                                         @Param("orderFreq") Long orderFreq,
                                                         @Param("pageSize") Integer pageSize);

    @Query(value = "SELECT p.* FROM product p WHERE p.status = '정상' AND p.expiration_date < :currentDate AND " +
            "p.store = (SELECT s.id FROM store s WHERE s.manager_id = :manager) ORDER BY p.name", nativeQuery = true)
    List<Product> findPassByManager(@Param("manager") User user,
                                   @Param("currentDate") LocalDate currentDate);

    @Query(value = "SELECT p.* FROM product p WHERE p.status = '정상' AND p.expiration_date < :currentDate AND " +
            "p.store = (SELECT t.store_id FROM part_timer t WHERE t.part_timer_id = :partTimer) ORDER BY p.name", nativeQuery = true)
    List<Product> findPassByPartTimer(@Param("partTimer") User user,
                                     @Param("currentDate") LocalDate currentDate);
}


