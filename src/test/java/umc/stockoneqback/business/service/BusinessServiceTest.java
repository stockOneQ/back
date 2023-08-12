package umc.stockoneqback.business.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.exception.BusinessErrorCode;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;
import umc.stockoneqback.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("Business [Service Layer] -> BusinessService 테스트")
class BusinessServiceTest extends ServiceTest {
    @Autowired
    private BusinessService businessService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFindService userFindService;

    private Long managerId;
    private Long supervisorId;

    @BeforeEach
    void setUp() {
        Long storeId = storeRepository.save(createStore("스타벅스 - 광화문점", "카페", "ABC123", "서울시 종로구")).getId();
        Long companyId = companyRepository.save(createCompany("A 납품업체", "과일", "ABC123")).getId();

        managerId = userService.saveManager(SAEWOO.toUser(), storeId);
        supervisorId = userService.saveSupervisor(ANNE.toUser(), "A 납품업체", "ABC123");
    }

    @Nested
    @DisplayName("슈퍼바이저 - 점주님 Business 등록")
    class register {
        @Test
        @DisplayName("이미 존재하는 Business가 있다면 등록에 실패한다")
        void throwExceptionByAlreadyExistBusiness() {
            // given
            businessService.register(supervisorId, managerId);

            // when - then
            assertThatThrownBy(() -> businessService.register(supervisorId, managerId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BusinessErrorCode.ALREADY_EXIST_BUSINESS.getMessage());
        }

        @Test
        @DisplayName("슈퍼바이저 - 점주님 Business 등록에 성공한다")
        void success() {
            // given
            businessService.register(supervisorId, managerId);

            // when
            User supervisor = userFindService.findById(supervisorId);
            User manager = userFindService.findById(managerId);
            Business findBusiness = businessRepository.findBySupervisorAndManager(supervisor, manager).orElseThrow();

            // then
            assertAll(
                    () -> assertThat(findBusiness.getSupervisor()).isEqualTo(supervisor),
                    () -> assertThat(findBusiness.getManager()).isEqualTo(manager),
                    () -> assertThat(findBusiness.getStatus()).isEqualTo(Status.NORMAL)
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
            assertThatThrownBy(() -> businessService.cancel(supervisorId, managerId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BusinessErrorCode.BUSINESS_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("슈퍼바이저 - 점주님 Business 삭제에 성공한다")
        void success() {
            // given
            businessService.register(supervisorId, managerId);

            // when
            businessService.cancel(supervisorId, managerId);

            // then
            User supervisor = userFindService.findById(supervisorId);
            User manager = userFindService.findById(managerId);
            assertThat(businessRepository.existsBySupervisorAndManager(supervisor, manager)).isFalse();
        }
    }

    @Test
    @DisplayName("Supervisor의 Business 목록 조회에 성공한다")
    void getBusinessBySupervisor() {
        // given
        User supervisor = userFindService.findById(supervisorId);
        User manager = userFindService.findById(managerId);
        businessService.register(supervisorId, managerId);

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
        User supervisor = userFindService.findById(supervisorId);
        User manager = userFindService.findById(managerId);
        businessService.register(supervisorId, managerId);

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
        User supervisor = userFindService.findById(supervisorId);
        User manager = userFindService.findById(managerId);
        Business business = businessRepository.save(Business.builder().supervisor(supervisor).manager(manager).build());

        // when
        businessService.deleteBusiness(business);

        // then
        assertThat(businessRepository.findById(business.getId()).isEmpty()).isTrue();
    }

    private Store createStore(String name, String sector, String code, String address) {
        return Store.builder()
                .name(name)
                .sector(sector)
                .code(code)
                .address(address)
                .build();
    }

    private Company createCompany(String name, String sector, String code) {
        return Company.builder()
                .name(name)
                .sector(sector)
                .code(code)
                .build();
    }
}