package umc.stockoneqback.role.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.exception.CompanyErrorCode;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static umc.stockoneqback.fixture.UserFixture.WIZ;

@DisplayName("Company [Service Layer] -> CompanyService 테스트")
class CompanyServiceTest extends ServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;

    private User user;
    private Company company;

    @BeforeEach
    void setUp() {
        company = companyRepository.save(createCompany("A 납품업체", "과일", "ABC123"));
        Long userId = userService.saveSupervisor(WIZ.toUser(), "A 납품업체", "ABC123");
        user = userRepository.findById(userId).orElseThrow();
    }

    @Test
    @DisplayName("회사 이름으로 회사를 조회한다")
    void findByName() {
        // when
        Company findCompany = companyService.findByName("A 납품업체");

        // then
        assertThat(findCompany).isEqualTo(company);
        assertThatThrownBy(() -> companyService.findByName("가짜 납품업체"))
                .isInstanceOf(BaseException.class)
                .hasMessage(CompanyErrorCode.COMPANY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회사에서 특정 슈퍼바이저를 삭제한다")
    void deleteSupervisorByUser() {
        // when
        companyService.deleteSupervisorByUser(company, user);
        Company findCompany = companyService.findByName("A 납품업체");

        // then
        assertThat(findCompany.getEmployees().contains(user)).isFalse();
    }

    private Company createCompany(String name, String sector, String code) {
        return Company.builder()
                .name(name)
                .sector(sector)
                .code(code)
                .build();
    }
}