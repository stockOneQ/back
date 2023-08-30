package umc.stockoneqback.business.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.infra.query.dto.BusinessList;
import umc.stockoneqback.business.service.dto.BusinessListResponse;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.*;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Business [Service Layer] -> BusinessListService 테스트")
class BusinessListServiceTest extends ServiceTest {
    @Autowired
    private BusinessListService businessListService;

    private static Company company;
    private final User[] managerList = new User[3];
    private final User[] supervisorList = new User[3];
    private final Store[] storeList = new Store[3];
    private static final String SEARCH = "위즈";

    @BeforeEach
    void setUp() throws InterruptedException {
        managerList[0] = userRepository.save(ANNE.toUser());
        managerList[1] = userRepository.save(ELLA.toUser());
        managerList[2] = userRepository.save(MIKE.toUser());

        supervisorList[0] = userRepository.save(WIZ.toUser());
        supervisorList[1] = userRepository.save(JACK.toUser());
        supervisorList[2] = userRepository.save(OLIVIA.toUser());

        storeList[0] = storeRepository.save(Z_YEONGTONG.toStore());
        storeList[1] = storeRepository.save(Z_SIHEUNG.toStore());
        storeList[2] = storeRepository.save(Y_YEONGTONG.toStore());

        company = companyRepository.save(new Company("CafeCompany", "카페", "QWE987"));

        for (int i = 0; i < 3; i++) {
            storeList[i].updateManager(managerList[i]);
            supervisorList[i].registerCompany(company);
            businessRepository.save(new Business(managerList[i], supervisorList[0]));
            Thread.sleep(1);
            businessRepository.save(new Business(managerList[i], supervisorList[1]));
            Thread.sleep(1);
            businessRepository.save(new Business(managerList[i], supervisorList[2]));
            Thread.sleep(1);
        }
    }

    @Test
    @DisplayName("해당 매니저와 연결된 슈퍼바이저 목록을 조회한다")
    void getSupervisors() {
        // when - then
        BusinessListResponse supervisors = businessListService.getSupervisors(managerList[0].getId(), Long.valueOf(-1), SEARCH);

        assertAll(
                () -> assertThat(supervisors.userList().size()).isEqualTo(1),
                () -> assertThat(supervisors.userList().get(0).getName()).isEqualTo(supervisorList[0].getName()),
                () -> assertThat(supervisors.userList().get(0).getStoreCoName()).isEqualTo(supervisorList[0].getCompany().getName())
        );
    }

    @Test
    @DisplayName("해당 슈퍼바이저와 연결된 점주 목록을 조회한다")
    void getManagers() {
        // when - then
        BusinessListResponse managers = businessListService.getManagers(supervisorList[1].getId(), -1L);

        int size = managers.userList().size();
        for (int i = 0; i < size; i++) {
            BusinessList manager = managers.userList().get(i);
            User user = managerList[size - i - 1];

            assertAll(
                    () -> assertThat(manager.getId()).isEqualTo(user.getId()),
                    () -> assertThat(manager.getName()).isEqualTo(user.getName()),
                    () -> assertThat(manager.getStoreCoName()).isEqualTo(user.getManagerStore().getName()),
                    () -> assertThat(manager.getPhoneNumber()).isEqualTo(user.getPhoneNumber())
            );
        }
    }
}