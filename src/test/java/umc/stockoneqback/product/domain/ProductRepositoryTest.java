package umc.stockoneqback.product.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.ANNE;

@DisplayName("Product [Repository Layer] -> ProductRepository 테스트")
public class ProductRepositoryTest extends RepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    private Store store;

    @BeforeEach
    void setup() {
        User manager = userRepository.save(ANNE.toUser());
        store = storeRepository.save(Z_YEONGTONG.toStore(manager));

        for (ProductFixture productFixture : ProductFixture.values())
            productRepository.save(productFixture.toProduct(store));
    }

    @Test
    @DisplayName("입력된 이름을 가지는 제품이 있다면 반환한다")
    void isExistProductByName() {
        // when - then
        Optional<Product> existProduct = productRepository.isExistProductByName(store, StoreCondition.ROOM.getValue(), "수박");
        Optional<Product> notExistProduct = productRepository.isExistProductByName(store, StoreCondition.ROOM.getValue(), "꽃게");

        assertAll(
                () -> assertThat(existProduct.isPresent()).isEqualTo(true),
                () -> assertThat(notExistProduct.isEmpty()).isEqualTo(true)
        );
    }

    @Test
    @DisplayName("입력된 productId를 가지는 제품이 있다면 반환한다")
    void findProductById() {
        // given
        Long savedProductId = productRepository.save(ProductFixture.APPLE.toProduct(store)).getId();

        // when - then
        Product findProduct = productRepository.findProductById(savedProductId).orElseThrow();
        assertThat(findProduct.getName()).isEqualTo("사과");
    }

    @Test
    @DisplayName("전체 제품 개수를 반환한다")
    void countProductAll() {
        // when
        Integer total = productRepository.countProductAll(store, StoreCondition.ROOM.getValue());

        // then
        assertThat(total).isEqualTo(16);
    }

    @Test
    @DisplayName("유통기한을 경과한 제품 개수를 반환한다")
    void countProductPass() {
        // when
        Integer total = productRepository.countProductPass(store, StoreCondition.ROOM.getValue(), LocalDate.now());

        // then
        assertThat(total).isEqualTo(2);
    }

    @Test
    @DisplayName("유통기한이 임박한 제품 개수를 반환한다")
    void countProductClose() {
        // when
        Integer total = productRepository.countProductClose(
                store, StoreCondition.ROOM.getValue(), LocalDate.now(), LocalDate.now().plusDays(3));

        // then
        assertThat(total).isEqualTo(5);
    }

    @Test
    @DisplayName("재고가 부족한 제품 개수를 반환한다")
    void countProductLack() {
        Integer total = productRepository.countProductLack(store, StoreCondition.ROOM.getValue());

        assertThat(total).isEqualTo(5);
    }
}