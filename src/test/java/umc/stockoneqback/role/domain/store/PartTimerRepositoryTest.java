package umc.stockoneqback.role.domain.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("PartTimer [Repository Layer] -> PartTimerRepository 테스트")
public class PartTimerRepositoryTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PartTimerRepository partTimerRepository;

    @Autowired
    private StoreRepository storeRepository;

    private User partTimer;
    private Store store;

    @BeforeEach
    void setup() {
        User manager = userRepository.save(ANNE.toUser());
        partTimer = userRepository.save(SAEWOO.toUser());

        store = storeRepository.save(Z_YEONGTONG.toStore(manager));
        partTimerRepository.save(PartTimer.createPartTimer(store, partTimer));
    }

    @Test
    @DisplayName("회원 ID를 통해 PartTimer 정보를 조회한다")
    void findByUser() {
        // when
        PartTimer findPartTimer = partTimerRepository.findByPartTimer(partTimer).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findPartTimer.getPartTimer()).isEqualTo(partTimer),
                () -> assertThat(findPartTimer.getStore()).isEqualTo(store)
        );
    }

    @Test
    @DisplayName("회원 ID를 통해 해당 회원을 PartTimer 역할에서 삭제한다")
    void deleteByUser() {
        // when
        partTimerRepository.deleteByPartTimer(partTimer);

        // then
        assertThat(partTimerRepository.findAll().isEmpty()).isTrue();
    }
}
