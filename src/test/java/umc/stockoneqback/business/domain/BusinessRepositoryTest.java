package umc.stockoneqback.business.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Business [Repository Test] -> BusinessRepository 테스트")
class BusinessRepositoryTest extends RepositoryTest {
    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    private User manager;
    private User supervisor;
    private User anonymous;
    private Business business;

    @BeforeEach
    void setUp() {
        manager = userRepository.save(SAEWOO.toUser());
        supervisor = userRepository.save(ANNE.toUser());
        anonymous = userRepository.save(WIZ.toUser());

        business = businessRepository.save(new Business(manager, supervisor));
    }

    @Test
    @DisplayName("슈퍼바이저와 매니저로 Business가 존재하는지 확인한다")
    void existsBySupervisorAndManager() {
        // when
        boolean actual1 = businessRepository.existsBySupervisorAndManager(supervisor, manager);
        boolean actual2 = businessRepository.existsBySupervisorAndManager(supervisor, anonymous);

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    @Test
    @DisplayName("슈퍼바이저와 매니저로 Business를 조회한다")
    void findBySupervisorAndManager() {
        // when
        Business findBusiness = businessRepository.findBySupervisorAndManager(supervisor, manager).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findBusiness.getSupervisor()).isEqualTo(supervisor),
                () -> assertThat(findBusiness.getManager()).isEqualTo(manager),
                () -> assertThat(findBusiness.getRelationStatus()).isEqualTo(RelationStatus.ACCEPT)
        );
    }

    @Test
    @DisplayName("슈퍼바이저와 매니저 Business를 삭제한다")
    void deleteBySupervisorAndManager() {
        // when
        businessRepository.deleteBySupervisorAndManager(supervisor, manager);

        // then
        assertAll(
                () -> assertThat(businessRepository.findAll()).hasSize(0),
                () -> assertThat(businessRepository.existsBySupervisorAndManager(supervisor, manager)).isFalse()
        );
    }

    @Test
    @DisplayName("슈퍼바이저를 통해 Business를 조회한다")
    void findBySupervisor() {
        // when
        List<Business> businessList = businessRepository.findBySupervisor(supervisor);

        // then
        assertAll(
                () -> assertThat(businessList.size()).isEqualTo(1),
                () -> assertThat(businessList.get(0).getSupervisor()).isEqualTo(supervisor),
                () -> assertThat(businessList.get(0).getManager()).isEqualTo(manager),
                () -> assertThat(businessList.get(0).getRelationStatus()).isEqualTo(RelationStatus.ACCEPT)
        );
    }

    @Test
    @DisplayName("매니저를 통해 Business를 조회한다")
    void findByManager() {
        // when
        List<Business> businessList = businessRepository.findByManager(manager);

        // then
        assertAll(
                () -> assertThat(businessList.size()).isEqualTo(1),
                () -> assertThat(businessList.get(0).getSupervisor()).isEqualTo(supervisor),
                () -> assertThat(businessList.get(0).getManager()).isEqualTo(manager),
                () -> assertThat(businessList.get(0).getRelationStatus()).isEqualTo(RelationStatus.ACCEPT)
        );
    }

    @Test
    @DisplayName("Business ID를 통해 Business를 삭제한다")
    void deleteByUser() {
        // when
        businessRepository.deleteById(business.getId());

        // then
        assertThat(businessRepository.findById(business.getId()).isEmpty()).isTrue();
    }
}