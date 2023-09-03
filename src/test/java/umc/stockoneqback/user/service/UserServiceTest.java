package umc.stockoneqback.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.D_PIZZA;
import static umc.stockoneqback.fixture.UserFixture.*;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("User [Service Layer] -> UserService 테스트")
class UserServiceTest extends ServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("가게 사장님 등록에 성공한다")
    void saveManager() {
        // when
        Long managerId = userService.saveManager(ELLA.toUser());

        // then
        User findManager = userRepository.findById(managerId).orElseThrow();
        assertAll(
                () -> assertThat(findManager.getName()).isEqualTo(ELLA.getName()),
                () -> assertThat(findManager.getLoginId()).isEqualTo(ELLA.getLoginId()),
                () -> assertThat(findManager.getPassword().isSamePassword(ELLA.getPassword(), ENCODER)).isTrue(),
                () -> assertThat(findManager.getBirth()).isEqualTo(ELLA.getBirth()),
                () -> assertThat(findManager.getPhoneNumber()).isEqualTo(ELLA.getPhoneNumber()),
                () -> assertThat(findManager.getRole()).isEqualTo(ELLA.getRole())
        );
    }

    @Nested
    @DisplayName("아르바이트생 등록")
    class savePartTimer {
        private Store store;

        @BeforeEach
        void setUp() {
            User manager = userRepository.save(ELLA.toUser());
            store = storeRepository.save(Store.createStore(D_PIZZA.getName(), D_PIZZA.getSector(), D_PIZZA.getAddress(), manager));
        }

        @Test
        @DisplayName("가게 코드가 일치하지 않으면 아르바이트생 등록에 실패한다")
        void throwExceptionByInvalidStoreCode() {
            // given
            User partTimer = SAEWOO.toUser();

            // when - then
            assertThatThrownBy(() -> userService.savePartTimer(partTimer, store.getName(), store.getCode() + "FAKE"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.INVALID_STORE_CODE.getMessage());
        }

        @Test
        @DisplayName("아르바이트생 등록에 성공한다")
        void success() {
            // when
            Long savedPartTimerId = userService.savePartTimer(SAEWOO.toUser(), store.getName(), store.getCode());

            // then
            User findPartTimer = userRepository.findById(savedPartTimerId).orElseThrow();
            Store findStore = storeRepository.findById(store.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findPartTimer.getName()).isEqualTo(SAEWOO.getName()),
                    () -> assertThat(findPartTimer.getLoginId()).isEqualTo(SAEWOO.getLoginId()),
                    () -> assertThat(findPartTimer.getPassword().isSamePassword(SAEWOO.getPassword(), ENCODER)).isTrue(),
                    () -> assertThat(findPartTimer.getBirth()).isEqualTo(SAEWOO.getBirth()),
                    () -> assertThat(findPartTimer.getPhoneNumber()).isEqualTo(SAEWOO.getPhoneNumber()),
                    () -> assertThat(findPartTimer.getRole()).isEqualTo(SAEWOO.getRole()),
                    () -> assertThat(findStore.getPartTimers().getPartTimers().size()).isEqualTo(1),
                    () -> assertThat(findStore.getPartTimers().getPartTimers().get(0).getPartTimer().getId()).isEqualTo(savedPartTimerId)
            );
        }
    }

    @Nested
    @DisplayName("슈퍼바이저 등록")
    class saveSupervisor {
        private Company company;

        @BeforeEach
        void setUp() {
            company = companyRepository.save(createCompany("A 납품업체", "과일", "ABC123"));
        }

        @Test
        @DisplayName("회사 코드가 일치하지 않으면 슈퍼바이저 등록에 실패한다")
        void throwExceptionByInvalidCompanyCode() {
            // given
            User supervisor = SAEWOO.toUser();

            // when - then
            assertThatThrownBy(() -> userService.saveSupervisor(supervisor, company.getName(), "ABC456"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.INVALID_COMPANY_CODE.getMessage());
        }

        @Test
        @DisplayName("슈퍼바이저 등록에 성공한다")
        void success() {
            // given
            User supervisor = JACK.toUser();

            // when
            Long saveSupervisorId = userService.saveSupervisor(supervisor, company.getName(), company.getCode());

            // then
            User findSupervisor = userRepository.findById(saveSupervisorId).orElseThrow();
            Company findCompany = companyRepository.findById(company.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findSupervisor.getName()).isEqualTo(JACK.getName()),
                    () -> assertThat(findSupervisor.getLoginId()).isEqualTo(JACK.getLoginId()),
                    () -> assertThat(findSupervisor.getPassword().isSamePassword(JACK.getPassword(), ENCODER)).isTrue(),
                    () -> assertThat(findSupervisor.getBirth()).isEqualTo(JACK.getBirth()),
                    () -> assertThat(findSupervisor.getPhoneNumber()).isEqualTo(JACK.getPhoneNumber()),
                    () -> assertThat(findSupervisor.getRole()).isEqualTo(JACK.getRole()),
                    () -> assertThat(findCompany.getEmployees().size()).isEqualTo(1),
                    () -> assertThat(findCompany.getEmployees()).contains(findSupervisor),
                    () -> assertThat(findSupervisor.getCompany()).isEqualTo(findCompany)
            );
        }
    }

    @Nested
    @DisplayName("로그인 아이디 찾기")
    class findLoginId {
        @Test
        @DisplayName("요청된 정보와 일치하는 사용자를 찾을 수 없으면 아이디 찾기에 실패한다")
        void throwExceptionByDuplicateLoginId() {

        }

    }

    @Test
    @DisplayName("사용자 관련 정보를 삭제하고 사용자 개인정보의 상태를 변경한다")
    void withdrawUser() {
        // given
        User user = userRepository.save(SAEWOO.toUser());
        authService.login(SAEWOO.getLoginId(), SAEWOO.getPassword());

        // when
        userService.withdrawUser(user.getId());

        // then
        User expiredUser = userRepository.findById(user.getId()).orElseThrow();
        assertAll(
                () -> assertThat(expiredUser.getStatus()).isEqualTo(Status.EXPIRED),
                () -> assertThat(tokenRepository.findByUserId(user.getId()).isEmpty()).isTrue()
        );
    }

    @Test
    @DisplayName("1년이 지난 탈퇴한 사용자의 개인정보를 완전히 삭제한다")
    void deleteExpiredUser() {
        // given
        User user = userRepository.save(SAEWOO.toUser());
        userService.withdrawUser(user.getId());
        userRepository.updateModifiedDateById(user.getId(), LocalDate.now().minusYears(2));

        // when
        userService.deleteExpiredUser();

        // then
        Optional<User> findUser = userRepository.findById(user.getId());
        assertThat(findUser.isEmpty()).isTrue();
    }

    private Company createCompany(String name, String sector, String code) {
        return Company.builder()
                .name(name)
                .sector(sector)
                .code(code)
                .build();
    }
}