package umc.stockoneqback.role.domain.company;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.fixture.UserFixture;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Company 도메인 테스트")
class CompanyTest {
    @Test
    @DisplayName("Company를 등록한다")
    void createCompany() {
        // when
        Company company = Company.createCompany("A 납품업체", "종이컵");

        // then
        assertAll(
                () -> assertThat(company.getName()).isEqualTo("A 납품업체"),
                () -> assertThat(company.getSector()).isEqualTo("종이컵"),
                () -> assertThat(company.getStatus()).isEqualTo(Status.NORMAL),
                () -> assertThat(company.getEmployees()).isEmpty()
        );
    }

    @Test
    @DisplayName("회사에 슈퍼바이저를 추가한다")
    void addEmployees() {
        // given
        Company company = Company.createCompany("A 납품업체", "종이컵");
        User partTimer1 = UserFixture.SAEWOO.toUser();
        User partTimer2 = UserFixture.ANNE.toUser();

        // when
        company.addEmployees(partTimer1);
        company.addEmployees(partTimer2);

        // then
        assertAll(
                () -> assertThat(company.getEmployees().size()).isEqualTo(2),
                () -> assertThat(company.getEmployees()).contains(partTimer1),
                () -> assertThat(company.getEmployees()).contains(partTimer2)
        );
    }

    @Test
    @DisplayName("Company에서 특정 슈퍼바이저를 삭제한다")
    void deleteSupervisor() {
        // given
        Company company = Company.createCompany("A 납품업체", "종이컵");
        User partTimer1 = UserFixture.SAEWOO.toUser();
        User partTimer2 = UserFixture.ANNE.toUser();
        company.addEmployees(partTimer1);
        company.addEmployees(partTimer2);

        // when
        company.deleteSupervisor(partTimer1);

        // then
        assertAll(
                () -> assertThat(company.getEmployees().size()).isEqualTo(1),
                () -> assertThat(company.getEmployees()).doesNotContain(partTimer1),
                () -> assertThat(company.getEmployees()).contains(partTimer2)
        );
    }
}