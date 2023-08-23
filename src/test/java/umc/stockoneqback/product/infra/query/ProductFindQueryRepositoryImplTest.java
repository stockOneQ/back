package umc.stockoneqback.product.infra.query;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.product.domain.SearchCondition;
import umc.stockoneqback.product.domain.SortCondition;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.infra.query.dto.ProductFindPage;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Product [Repository Layer] -> ProductFindQueryRepository 테스트")
public class ProductFindQueryRepositoryImplTest extends RepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StoreRepository storeRepository;

    private static final int PAGE_SIZE = 12;

    @BeforeEach
    void setup() {
        Store zStore = storeRepository.save(Z_YEONGTONG.toStore());
        for (ProductFixture productFixture : ProductFixture.values())
            productRepository.save(productFixture.toProduct(zStore));
    }

    @AfterEach
    void clearAll() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("입력된 이름을 포함하는 모든 제품 목록을 반환한다")
    void findProductByName() {
        Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
        List<ProductFindPage> result = productRepository.findProductByName(zStore, StoreCondition.ROOM, "리");

        assertAll(
                () -> assertThat(result.get(0).getName()).isEqualTo("두리안"),
                () -> assertThat(result.get(1).getName()).isEqualTo("블루베리"),
                () -> assertThat(result.get(2).getName()).isEqualTo("체리")
        );
    }

    @Nested
    @DisplayName("제품 목록 조회")
    class productList {
        @Test
        @DisplayName("전체 제품을 가나다순으로 12개씩 반환한다")
        void findPageOfAllOrderByName() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<ProductFindPage> firstPage = productRepository.findPageOfSearchConditionOrderBySortCondition
                    (zStore, StoreCondition.ROOM, SearchCondition.ALL, SortCondition.NAME, null, null, PAGE_SIZE);

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

        /*
        @Test
        @DisplayName("전체 제품을 빈도순으로 12개씩 반환한다")
        void findPageOfAllOrderByOrderFreq() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<ProductFindPage> firstPage = productRepository.findPageOfSearchConditionOrderBySortCondition
                    (zStore, StoreCondition.ROOM, SearchCondition.ALL, SortCondition.ORDER_FREQUENCY, null, null, PAGE_SIZE);

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

         */

        @Test
        @DisplayName("유통기한을 경과한 제품을 가나다순으로 12개씩 반환한다")
        void findPageOfPassOrderByName() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<ProductFindPage> firstPage = productRepository.findPageOfSearchConditionOrderBySortCondition
                    (zStore, StoreCondition.ROOM, SearchCondition.PASS, SortCondition.NAME, null, null, PAGE_SIZE);

            assertAll(
                    () -> assertThat(firstPage.get(0).getName()).isEqualTo("감"),
                    () -> assertThat(firstPage.get(1).getName()).isEqualTo("포도"),
                    () -> assertThat(firstPage.size()).isEqualTo(2)
            );
        }

        /*
        @Test
        @DisplayName("유통기한을 경과한 제품을 빈도순으로 12개씩 반환한다")
        void findPageOfPassOrderByOrderFreq() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<ProductFindPage> firstPage = productRepository.findPageOfSearchConditionOrderBySortCondition
                    (zStore, StoreCondition.ROOM, SearchCondition.PASS, SortCondition.ORDER_FREQUENCY, null, null, PAGE_SIZE);

            assertAll(
                    () -> assertThat(firstPage.get(0).getName()).isEqualTo("감"),
                    () -> assertThat(firstPage.get(1).getName()).isEqualTo("포도"),
                    () -> assertThat(firstPage.size()).isEqualTo(2)
            );
        }

         */

        @Test
        @DisplayName("유통기한이 임박한 제품을 가나다순으로 12개씩 반환한다")
        void findPageOfCloseOrderByName() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<ProductFindPage> firstPage = productRepository.findPageOfSearchConditionOrderBySortCondition
                    (zStore, StoreCondition.ROOM, SearchCondition.CLOSE, SortCondition.NAME, null, null, PAGE_SIZE);

            assertAll(
                    () -> assertThat(firstPage.get(0).getName()).isEqualTo("두리안"),
                    () -> assertThat(firstPage.get(1).getName()).isEqualTo("메론"),
                    () -> assertThat(firstPage.get(2).getName()).isEqualTo("사과"),
                    () -> assertThat(firstPage.get(3).getName()).isEqualTo("자두"),
                    () -> assertThat(firstPage.get(4).getName()).isEqualTo("체리"),
                    () -> assertThat(firstPage.size()).isEqualTo(5)
            );
        }

        /*
        @Test
        @DisplayName("유통기한이 임박한 제품을 빈도순으로 12개씩 반환한다")
        void findPageOfCloseOrderByOrderFreq() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<ProductFindPage> firstPage = productRepository.findPageOfSearchConditionOrderBySortCondition
                    (zStore, StoreCondition.ROOM, SearchCondition.CLOSE, SortCondition.ORDER_FREQUENCY, null, null, PAGE_SIZE);

            assertAll(
                    () -> assertThat(firstPage.get(0).getName()).isEqualTo("두리안"),
                    () -> assertThat(firstPage.get(1).getName()).isEqualTo("사과"),
                    () -> assertThat(firstPage.get(2).getName()).isEqualTo("메론"),
                    () -> assertThat(firstPage.get(3).getName()).isEqualTo("자두"),
                    () -> assertThat(firstPage.get(4).getName()).isEqualTo("체리"),
                    () -> assertThat(firstPage.size()).isEqualTo(5)
            );
        }

         */

        @Test
        @DisplayName("재고가 부족한 제품을 가나다순으로 12개씩 반환한다")
        void findPageOfLackOrderByName() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<ProductFindPage> firstPage = productRepository.findPageOfSearchConditionOrderBySortCondition
                    (zStore, StoreCondition.ROOM, SearchCondition.LACK, SortCondition.NAME, null, null, PAGE_SIZE);

            assertAll(
                    () -> assertThat(firstPage.get(0).getName()).isEqualTo("배"),
                    () -> assertThat(firstPage.get(1).getName()).isEqualTo("복숭아"),
                    () -> assertThat(firstPage.get(2).getName()).isEqualTo("사과"),
                    () -> assertThat(firstPage.get(3).getName()).isEqualTo("체리"),
                    () -> assertThat(firstPage.get(4).getName()).isEqualTo("파인애플"),
                    () -> assertThat(firstPage.size()).isEqualTo(5)
            );
        }

        /*
        @Test
        @DisplayName("재고가 부족한 제품을 빈도순으로 12개씩 반환한다")
        void findPageOfLackOrderByOrderFreq() {
            Store zStore = storeRepository.findByName(Z_YEONGTONG.getName()).orElseThrow();
            List<ProductFindPage> firstPage = productRepository.findPageOfSearchConditionOrderBySortCondition
                    (zStore, StoreCondition.ROOM, SearchCondition.LACK, SortCondition.ORDER_FREQUENCY, null, null, PAGE_SIZE);

            assertAll(
                    () -> assertThat(firstPage.get(0).getName()).isEqualTo("배"),
                    () -> assertThat(firstPage.get(1).getName()).isEqualTo("파인애플"),
                    () -> assertThat(firstPage.get(2).getName()).isEqualTo("사과"),
                    () -> assertThat(firstPage.get(3).getName()).isEqualTo("복숭아"),
                    () -> assertThat(firstPage.get(4).getName()).isEqualTo("체리"),
                    () -> assertThat(firstPage.size()).isEqualTo(5)
            );
        }

         */
    }
}
