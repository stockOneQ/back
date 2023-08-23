package umc.stockoneqback.product.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;

@DisplayName("Product [Repository Layer] -> ProductRepository 테스트")
public class ProductRepositoryTest extends RepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @BeforeEach
    void setup() {
        Store zStore = storeRepository.save(Z_YEONGTONG.toStore());
        for (ProductFixture productFixture : ProductFixture.values())
            productRepository.save(productFixture.toProduct(zStore));
    }

    @Test
    @DisplayName("입력된 이름을 가지는 제품이 있다면 반환한다")
    void isExistProductByName() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        Optional<Product> existProduct =
                productRepository.isExistProductByName(zStore, StoreCondition.ROOM.getValue(), "수박");
        Optional<Product> notExistProduct =
                productRepository.isExistProductByName(zStore, StoreCondition.ROOM.getValue(), "꽃게");

        assertAll(
                () -> assertThat(existProduct.isPresent()).isEqualTo(true),
                () -> assertThat(notExistProduct.isEmpty()).isEqualTo(true)
        );
    }

    @Test
    @DisplayName("입력된 productId를 가지는 제품이 있다면 반환한다")
    void findProductById() {
        Store zStore = storeRepository.save(Z_YEONGTONG.toStore());
        Long savedProductId = productRepository.save(ProductFixture.APPLE.toProduct(zStore)).getId();

        Product findProduct = productRepository.findProductById(savedProductId).orElseThrow();
        assertThat(findProduct.getName()).isEqualTo("사과");
    }

    @Test
    @DisplayName("전체 제품 개수를 반환한다")
    void countProductAll() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        Integer total = productRepository.countProductAll(zStore, StoreCondition.ROOM.getValue());

        assertThat(total).isEqualTo(16);
    }

    @Test
    @DisplayName("유통기한을 경과한 제품 개수를 반환한다")
    void countProductPass() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        Integer total = productRepository.countProductPass(zStore, StoreCondition.ROOM.getValue(), LocalDate.now());

        assertThat(total).isEqualTo(2);
    }

    @Test
    @DisplayName("유통기한이 임박한 제품 개수를 반환한다")
    void countProductClose() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        Integer total = productRepository.countProductClose
                (zStore, StoreCondition.ROOM.getValue(), LocalDate.now(), LocalDate.now().plusDays(3));

        assertThat(total).isEqualTo(5);
    }

    @Test
    @DisplayName("재고가 부족한 제품 개수를 반환한다")
    void countProductLack() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        Integer total = productRepository.countProductLack(zStore, StoreCondition.ROOM.getValue());

        assertThat(total).isEqualTo(5);
    }

}