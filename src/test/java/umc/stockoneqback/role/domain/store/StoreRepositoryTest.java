package umc.stockoneqback.role.domain.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.global.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Store [Repository Layer] -> StoreRepository 테스트")
class StoreRepositoryTest extends RepositoryTest {
    @Autowired
    private StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        storeRepository.save(createStore("투썸플레이스 - 강남역점", "카페", "ABC123", "서울시 강남구"));
        storeRepository.save(createStore("투썸플레이스 - 홍대점", "카페", "ABC123", "서울시 마포구"));
    }

    @Test
    @DisplayName("가게 이름으로 가게가 존재하는지 확인한다")
    void existsByName() {
        // when
        boolean actual1 = storeRepository.existsByName("투썸플레이스 - 강남역점");
        boolean actual2 = storeRepository.existsByName("투썸플레이스 - 홍대점");
        boolean actual3 = storeRepository.existsByName("투썸플레이스 - 신촌점");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse()
        );
    }

    @Test
    @DisplayName("가게 이름으로 가게를 조회한다")
    void findByName() {
        // when
        Store findStore = storeRepository.findByName("투썸플레이스 - 강남역점").orElseThrow();

        // then
        assertAll(
                () -> assertThat(findStore.getSector()).isEqualTo("카페"),
                () -> assertThat(findStore.getCode()).isEqualTo("ABC123"),
                () -> assertThat(findStore.getAddress()).isEqualTo("서울시 강남구"),
                () -> assertThat(findStore.getStatus()).isEqualTo(Status.NORMAL)
        );
    }

    private Store createStore(String name, String sector, String code, String address) {
        return Store.builder()
                .name(name)
                .sector(sector)
                .code(code)
                .address(address)
                .build();
    }
}