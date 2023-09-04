package umc.stockoneqback.business.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.exception.BusinessErrorCode;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.WIZ;

@DisplayName("Business [Service Layer] -> BusinessService 테스트")
class BusinessServiceTest extends ServiceTest {
    @Autowired
    private BusinessService businessService;

    @Autowired
    private UserFindService userFindService;

    private User manager;
    private User supervisor;

    @BeforeEach
    void setUp() {
        manager = userRepository.save(ANNE.toUser());
        storeRepository.save(Z_SIHEUNG.toStore(manager));

        Company company = companyRepository.save(createCompany("A 납품업체", "과일", "ABC123"));
        supervisor = userRepository.save(WIZ.toUser());
        supervisor.registerCompany(company);
    }

    @Nested
    @DisplayName("슈퍼바이저 - 점주님 Business 등록")
    class register {
        @Test
        @DisplayName("이미 존재하는 Business가 있다면 등록에 실패한다")
        void throwExceptionByAlreadyExistBusiness() {
            // given
            businessService.register(supervisor.getId(), manager.getId());

            // when - then
            assertThatThrownBy(() -> businessService.register(supervisor.getId(), manager.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BusinessErrorCode.ALREADY_EXIST_BUSINESS.getMessage());
        }

        @Test
        @DisplayName("슈퍼바이저 - 점주님 Business 등록에 성공한다")
        void success() {
            // given
            businessService.register(supervisor.getId(), manager.getId());

            // when
            User findSupervisor = userFindService.findById(supervisor.getId());
            User findManager = userFindService.findById(manager.getId());
            Business findBusiness = businessRepository.findBySupervisorAndManager(findSupervisor, findManager).orElseThrow();

            // then
            assertAll(
                    () -> assertThat(findBusiness.getSupervisor()).isEqualTo(findSupervisor),
                    () -> assertThat(findBusiness.getManager()).isEqualTo(findManager),
                    () -> assertThat(findBusiness.getRelationStatus()).isEqualTo(RelationStatus.ACCEPT)
            );
        }
    }

    @Nested
    @DisplayName("슈퍼바이저 - 점주님 Business 삭제")
    class cancel {
        @Test
        @DisplayName("Business가 존재하지 않는다면 Business 삭제에 실패한다")
        void throwExceptionByBusinessNotFound() {
            // when - then
            assertThatThrownBy(() -> businessService.cancel(supervisor.getId(), manager.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BusinessErrorCode.BUSINESS_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("슈퍼바이저 - 점주님 Business 삭제에 성공한다")
        void success() {
            // given
            businessService.register(supervisor.getId(), manager.getId());

            // when
            businessService.cancel(supervisor.getId(), manager.getId());

            // then
            assertThat(businessRepository.existsBySupervisorAndManager(supervisor, manager)).isFalse();
        }
    }

    @Test
    @DisplayName("Supervisor의 Business 목록 조회에 성공한다")
    void getBusinessBySupervisor() {
        // given
        businessService.register(supervisor.getId(), manager.getId());

        // when
        List<Business> businessList = businessService.getBusinessBySupervisor(supervisor);

        // then
        assertAll(
                () -> assertThat(businessList.size()).isEqualTo(1),
                () -> assertThat(businessList.get(0).getSupervisor()).isEqualTo(supervisor),
                () -> assertThat(businessList.get(0).getManager()).isEqualTo(manager)
        );
    }

    @Test
    @DisplayName("Manager의 Business 목록 조회에 성공한다")
    void getBusinessByManager() {
        // given
        businessService.register(supervisor.getId(), manager.getId());

        // when
        List<Business> businessList = businessService.getBusinessByManager(manager);

        // then
        assertAll(
                () -> assertThat(businessList.size()).isEqualTo(1),
                () -> assertThat(businessList.get(0).getSupervisor()).isEqualTo(supervisor),
                () -> assertThat(businessList.get(0).getManager()).isEqualTo(manager)
        );
    }

    @Test
    @DisplayName("Business ID를 통한 Business 삭제에 성공한다")
    void deleteAll() {
        // given
        Business business = businessRepository.save(Business.builder().supervisor(supervisor).manager(manager).build());

        // when
        businessService.deleteBusiness(business);

        // then
        assertThat(businessRepository.findById(business.getId()).isEmpty()).isTrue();
    }

    private Company createCompany(String name, String sector, String code) {
        return Company.builder()
                .name(name)
                .sector(sector)
                .code(code)
                .build();
    }
}