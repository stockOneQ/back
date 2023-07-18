package umc.stockoneqback.role.domain.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Company [Repository Layer] -> CompanyRepository 테스트")
class CompanyRepositoryTest extends RepositoryTest {
    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    void setUp() {
        companyRepository.save(createCompany("A 납품업체", "과일", "ABC123"));
    }

    @Test
    @DisplayName("회사 이름으로 기업을 조회한다")
    void findByName() {
        // when
        Company findCompany = companyRepository.findByName("A 납품업체").orElseThrow();

        // then
        assertAll(
                () -> assertThat(findCompany.getSector()).isEqualTo("과일"),
                () -> assertThat(findCompany.getCode()).isEqualTo("ABC123")
        );
    }

    private Company createCompany(String name, String sector, String code) {
        return Company.builder()
                .name(name)
                .sector(sector)
                .code(code)
                .build();
    }
}
