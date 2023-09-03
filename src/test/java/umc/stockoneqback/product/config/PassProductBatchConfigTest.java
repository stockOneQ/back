package umc.stockoneqback.product.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.common.EmbeddedRedisConfig;
import umc.stockoneqback.common.TestBatchLegacyConfig;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.company.CompanyRepository;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.UserFixture.BOB;
import static umc.stockoneqback.fixture.UserFixture.JACK;

@Import({PassProductBatchConfig.class, TestBatchLegacyConfig.class, EmbeddedRedisConfig.class})
@SpringBootTest
@DisplayName("Product [Config Layer] -> PassProductBatchConfig 테스트")
public class PassProductBatchConfigTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    protected ProductRepository productRepository;

    private final ProductFixture[] productFixtures = ProductFixture.values();

    private static User manager;
    private static User supervisor;
    private static Store store;
    private static Company company;

    @BeforeEach
    void setup() {
        manager = userRepository.save(BOB.toUser());
        store = storeRepository.save(Store.createStore(Z_SIHEUNG.getName(), Z_SIHEUNG.getSector(), Z_SIHEUNG.getAddress(), manager));

        company = companyRepository.save(new Company("A 납품업체", "과일", "ABC123"));
        supervisor = userRepository.save(JACK.toUser());

        for (int i = 0; i < productFixtures.length; i++)
            productRepository.save(productFixtures[i].toProduct(store));
    }

    @Nested
    @DisplayName("현재 접속중인 사용자별 유통기한 경과 제품 목록 푸시 알림을 위한 배치 실행")
    class pushListOfPassProductByOnlineUsers {
        @Test
        @DisplayName("배치 실행에 성공한다")
        void success() throws Exception {
            Map<String, JobParameter> confMap = new HashMap<>();
            confMap.put("time", new JobParameter(System.currentTimeMillis()));
            JobParameters jobParameters = new JobParameters(confMap);

            JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

            assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
            assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        }
    }
}
