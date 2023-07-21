package umc.stockoneqback.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.fixture.UserFixture.WIZ;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("User [Service Layer] -> UserService 테스트")
class UserServiceTest extends ServiceTest {
    @Autowired
    private UserService userService;

    private Store store;
    private Company company;

    @BeforeEach
    void setUp() {
        store = storeRepository.save(createStore("스타벅스 - 광화문점", "카페", "ABC123", "서울시 종로구"));
        company = companyRepository.save(createCompany("A 납품업체", "과일", "ABC123"));
    }

    @Test
    @DisplayName("가게 사장님 등록에 성공한다")
    void saveManager() {
        // when
        User manager = SAEWOO.toUser();
        userService.saveManager(manager, store.getId());

        // then
        Store findStore = storeRepository.findById(store.getId()).orElseThrow();
        assertThat(findStore.getManager()).isEqualTo(manager);
    }

    @Nested
    @DisplayName("아르바이트생 등록")
    class savePartTimer {
        @Test
        @DisplayName("가게 코드가 일치하지 않으면 아르바이트생 등록에 실패한다")
        void throwExceptionByInvalidStoreCode() {
            // given
            User partTimer = SAEWOO.toUser();

            // when - then
            assertThatThrownBy(() -> userService.savePartTimer(partTimer, "스타벅스 - 광화문점", "ABC456"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.INVALID_STORE_CODE.getMessage());
        }

        @Test
        @DisplayName("아르바이트생 등록에 성공한다")
        void success() {
            // when
            Long savedUserId = userService.savePartTimer(SAEWOO.toUser(), store.getName(), store.getCode());

            // then
            Store findStore = storeRepository.findById(store.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findStore.getPartTimers().getPartTimers().size()).isEqualTo(1),
                    () -> assertThat(findStore.getPartTimers().getPartTimers().get(0).getPartTimer().getId()).isEqualTo(savedUserId)
            );
        }
    }

    @Nested
    @DisplayName("슈퍼바이저 등록")
    class saveSupervisor {
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
            User supervisor = SAEWOO.toUser();

            // when
            Long saveSupervisorId = userService.saveSupervisor(supervisor, company.getName(), company.getCode());

            // then
            User findSupervisor = userRepository.findById(saveSupervisorId).orElseThrow();
            Company findCompany = companyRepository.findById(company.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findCompany.getEmployees().size()).isEqualTo(1),
                    () -> assertThat(findCompany.getEmployees()).contains(findSupervisor),
                    () -> assertThat(findSupervisor.getCompany()).isEqualTo(findCompany)
            );
        }
    }

    @Nested
    @DisplayName("회원 정보 수정")
    class updateInformation {
        @Test
        @DisplayName("중복된 로그인 아이디가 존재한다면 회원 정보 수정에 실패한다")
        void throwExceptionByDuplicateLoginId() {
            // given
            Long userId = userRepository.save(SAEWOO.toUser()).getId();
            userRepository.save(WIZ.toUser());

            // when - then
            assertThatThrownBy(() -> userService.updateInformation(userId, WIZ.getName(), WIZ.getBirth(), WIZ.getEmail(), WIZ.getLoginId(), WIZ.getPassword(), WIZ.getPhoneNumber()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.DUPLICATE_LOGIN_ID.getMessage());
        }

        @Test
        @DisplayName("회원 정보 수정에 성공한다")
        void success() {
            // given
            Long userId = userRepository.save(SAEWOO.toUser()).getId();

            // when
            userService.updateInformation(userId, WIZ.getName(), WIZ.getBirth(), WIZ.getEmail(), WIZ.getLoginId(), WIZ.getPassword(), WIZ.getPhoneNumber());

            // then
            User findUser = userRepository.findById(userId).orElseThrow();
            assertAll(
                    () -> assertThat(findUser.getName()).isEqualTo(WIZ.getName()),
                    () -> assertThat(findUser.getBirth()).isEqualTo(WIZ.getBirth()),
                    () -> assertThat(findUser.getEmail().getValue()).isEqualTo(WIZ.getEmail()),
                    () -> assertThat(findUser.getLoginId()).isEqualTo(WIZ.getLoginId()),
                    () -> assertThat(findUser.getPassword().isSamePassword(WIZ.getPassword(), ENCODER)).isTrue(),
                    () -> assertThat(findUser.getPhoneNumber()).isEqualTo(WIZ.getPhoneNumber())
            );
        }
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