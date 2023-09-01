package umc.stockoneqback.business.infra.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.business.infra.query.dto.FilteredBusinessUser;
import umc.stockoneqback.business.infra.query.dto.FindBusinessUser;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.role.domain.store.PartTimer;
import umc.stockoneqback.role.domain.store.PartTimerRepository;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.G_TTEOKBOKKI;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Business [Repository Layer] -> BusinessFindQueryRepository 테스트")
class BusinessFindQueryRepositoryImplTest extends RepositoryTest {
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartTimerRepository partTimerRepository;
    @Autowired
    private StoreRepository storeRepository;

    private User manager;
    private User supervisor;
    private User partTimer;
    private Store store;
    private Business business;

    @BeforeEach
    void setUp() {
        manager = userRepository.save(ELLA.toUser());
        supervisor = userRepository.save(JACK.toUser());
        partTimer = userRepository.save((BOB.toUser()));
        store = storeRepository.save(G_TTEOKBOKKI.toStore());
        partTimerRepository.save(PartTimer.createPartTimer(store, partTimer));
        store.updateManager(manager);
        store.updatePartTimer(partTimer);
        business = businessRepository.save(new Business(manager, supervisor));
    }

    @Test
    @DisplayName("해당 매니저와 비즈니스 관계를 맺는 슈퍼바이저를 찾는다")
    void findBusinessByManager() {
        // given - when
        FilteredBusinessUser<FindBusinessUser> filteredSupervisor = businessRepository.findBusinessByManager(manager.getId());

        // then
        assertAll(
                () -> assertThat(filteredSupervisor.getTotal()).isEqualTo(1L),
                () -> assertThat(filteredSupervisor.getBusinessUserList().size()).isEqualTo(1),
                () -> assertThat(filteredSupervisor.getBusinessUserList().get(0).getName()).isEqualTo(supervisor.getName())
        );
    }

    @Test
    @DisplayName("해당 파트타이머와 비즈니스 관계에 있는 슈퍼바이저를 찾는다")
    void findBusinessByPartTimer() {
        // given - when
        FilteredBusinessUser<FindBusinessUser> filteredSupervisor = businessRepository.findBusinessByPartTimer(partTimer.getId());

        // then
        assertAll(
                () -> assertThat(filteredSupervisor.getTotal()).isEqualTo(1L),
                () -> assertThat(filteredSupervisor.getBusinessUserList().size()).isEqualTo(1),
                () -> assertThat(filteredSupervisor.getBusinessUserList().get(0).getName()).isEqualTo(supervisor.getName())
        );
    }

    @Test
    @DisplayName("해당 슈퍼바이저와 비즈니스 관계를 맺는 매니저를 찾는다")
    void findBusinessBySupervisor() {
        // given - when
        FilteredBusinessUser<FindBusinessUser> filteredManager = businessRepository.findBusinessBySupervisor(supervisor.getId());

        // then
        assertAll(
                () -> assertThat(filteredManager.getTotal()).isEqualTo(1L),
                () -> assertThat(filteredManager.getBusinessUserList().size()).isEqualTo(1),
                () -> assertThat(filteredManager.getBusinessUserList().get(0).getName()).isEqualTo(manager.getName())
        );
    }
}