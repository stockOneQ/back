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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.*;

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
    @DisplayName("입력된 이름을 포함하는 모든 제품 목록을 반환한다")
    void findProductByName() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<Product> result = productRepository.findProductByName(zStore, StoreCondition.ROOM.getValue(), "리");

        assertAll(
                () -> assertThat(result.get(0).getName()).isEqualTo("두리안"),
                () -> assertThat(result.get(1).getName()).isEqualTo("블루베리"),
                () -> assertThat(result.get(2).getName()).isEqualTo("체리")
        );
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
        Product findProduct = productRepository.findProductById(1L).orElseThrow();

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

    @Test
    @DisplayName("전체 제품을 가나다순으로 12개씩 반환한다")
    void findPageOfAllOrderByName() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<Product> firstPage = productRepository.findPageOfAllOrderByName
                (zStore, StoreCondition.ROOM.getValue(), null, 12);

        assertAll(
                () -> assertThat(firstPage.get(0).getName()).isEqualTo("감"),
                () -> assertThat(firstPage.get(1).getName()).isEqualTo("귤"),
                () -> assertThat(firstPage.get(2).getName()).isEqualTo("두리안"),
                () -> assertThat(firstPage.get(3).getName()).isEqualTo("망고"),
                () -> assertThat(firstPage.get(4).getName()).isEqualTo("메론"),
                () -> assertThat(firstPage.get(5).getName()).isEqualTo("바나나"),
                () -> assertThat(firstPage.get(6).getName()).isEqualTo("배"),
                () -> assertThat(firstPage.get(7).getName()).isEqualTo("복숭아"),
                () -> assertThat(firstPage.get(8).getName()).isEqualTo("블루베리"),
                () -> assertThat(firstPage.get(9).getName()).isEqualTo("사과"),
                () -> assertThat(firstPage.get(10).getName()).isEqualTo("수박"),
                () -> assertThat(firstPage.get(11).getName()).isEqualTo("오렌지"),
                () -> assertThat(firstPage.size()).isEqualTo(12)
        );
    }

    @Test
    @DisplayName("전체 제품을 빈도순으로 12개씩 반환한다")
    void findPageOfAllOrderByOrderFreq() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<Product> firstPage = productRepository.findPageOfAllOrderByOrderFreq
                (zStore, StoreCondition.ROOM.getValue(), null, null, 12);

        assertAll(
                () -> assertThat(firstPage.get(0).getName()).isEqualTo("바나나"),
                () -> assertThat(firstPage.get(1).getName()).isEqualTo("수박"),
                () -> assertThat(firstPage.get(2).getName()).isEqualTo("배"),
                () -> assertThat(firstPage.get(3).getName()).isEqualTo("블루베리"),
                () -> assertThat(firstPage.get(4).getName()).isEqualTo("파인애플"),
                () -> assertThat(firstPage.get(5).getName()).isEqualTo("감"),
                () -> assertThat(firstPage.get(6).getName()).isEqualTo("두리안"),
                () -> assertThat(firstPage.get(7).getName()).isEqualTo("사과"),
                () -> assertThat(firstPage.get(8).getName()).isEqualTo("오렌지"),
                () -> assertThat(firstPage.get(9).getName()).isEqualTo("귤"),
                () -> assertThat(firstPage.get(10).getName()).isEqualTo("메론"),
                () -> assertThat(firstPage.get(11).getName()).isEqualTo("포도"),
                () -> assertThat(firstPage.size()).isEqualTo(12)
        );
    }

    @Test
    @DisplayName("유통기한을 경과한 제품을 가나다순으로 12개씩 반환한다")
    void findPageOfPassOrderByName() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<Product> firstPage = productRepository.findPageOfPassOrderByName
                (zStore, StoreCondition.ROOM.getValue(), null, 12, LocalDate.now());

        assertAll(
                () -> assertThat(firstPage.get(0).getName()).isEqualTo("감"),
                () -> assertThat(firstPage.get(1).getName()).isEqualTo("포도"),
                () -> assertThat(firstPage.size()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("유통기한을 경과한 제품을 빈도순으로 12개씩 반환한다")
    void findPageOfPassOrderByOrderFreq() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<Product> firstPage = productRepository.findPageOfPassOrderByOrderFreq
                (zStore, StoreCondition.ROOM.getValue(), null, null, 12, LocalDate.now());

        assertAll(
                () -> assertThat(firstPage.get(0).getName()).isEqualTo("감"),
                () -> assertThat(firstPage.get(1).getName()).isEqualTo("포도"),
                () -> assertThat(firstPage.size()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("유통기한이 임박한 제품을 가나다순으로 12개씩 반환한다")
    void findPageOfCloseOrderByName() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<Product> firstPage = productRepository.findPageOfCloseOrderByName
                (zStore, StoreCondition.ROOM.getValue(), null, 12,
                        LocalDate.now(), LocalDate.now().plusDays(3));

        assertAll(
                () -> assertThat(firstPage.get(0).getName()).isEqualTo("두리안"),
                () -> assertThat(firstPage.get(1).getName()).isEqualTo("메론"),
                () -> assertThat(firstPage.get(2).getName()).isEqualTo("사과"),
                () -> assertThat(firstPage.get(3).getName()).isEqualTo("자두"),
                () -> assertThat(firstPage.get(4).getName()).isEqualTo("체리"),
                () -> assertThat(firstPage.size()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("유통기한이 임박한 제품을 빈도순으로 12개씩 반환한다")
    void findPageOfCloseOrderByOrderFreq() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<Product> firstPage = productRepository.findPageOfCloseOrderByOrderFreq
                (zStore, StoreCondition.ROOM.getValue(), null, null, 12,
                        LocalDate.now(), LocalDate.now().plusDays(3));

        assertAll(
                () -> assertThat(firstPage.get(0).getName()).isEqualTo("두리안"),
                () -> assertThat(firstPage.get(1).getName()).isEqualTo("사과"),
                () -> assertThat(firstPage.get(2).getName()).isEqualTo("메론"),
                () -> assertThat(firstPage.get(3).getName()).isEqualTo("자두"),
                () -> assertThat(firstPage.get(4).getName()).isEqualTo("체리"),
                () -> assertThat(firstPage.size()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("재고가 부족한 제품을 가나다순으로 12개씩 반환한다")
    void findPageOfLackOrderByName() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<Product> firstPage = productRepository.findPageOfLackOrderByName
                (zStore, StoreCondition.ROOM.getValue(), null, 12);

        assertAll(
                () -> assertThat(firstPage.get(0).getName()).isEqualTo("배"),
                () -> assertThat(firstPage.get(1).getName()).isEqualTo("복숭아"),
                () -> assertThat(firstPage.get(2).getName()).isEqualTo("사과"),
                () -> assertThat(firstPage.get(3).getName()).isEqualTo("체리"),
                () -> assertThat(firstPage.get(4).getName()).isEqualTo("파인애플"),
                () -> assertThat(firstPage.size()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("재고가 부족한 제품을 빈도순으로 12개씩 반환한다")
    void findPageOfLackOrderByOrderFreq() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<Product> firstPage = productRepository.findPageOfLackOrderByOrderFreq
                (zStore, StoreCondition.ROOM.getValue(), null, null, 12);

        assertAll(
                () -> assertThat(firstPage.get(0).getName()).isEqualTo("배"),
                () -> assertThat(firstPage.get(1).getName()).isEqualTo("파인애플"),
                () -> assertThat(firstPage.get(2).getName()).isEqualTo("사과"),
                () -> assertThat(firstPage.get(3).getName()).isEqualTo("복숭아"),
                () -> assertThat(firstPage.get(4).getName()).isEqualTo("체리"),
                () -> assertThat(firstPage.size()).isEqualTo(5)
        );
    }

}