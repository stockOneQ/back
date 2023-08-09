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
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.auth.service.TokenService;
import umc.stockoneqback.common.DatabaseCleaner;
import umc.stockoneqback.common.EmbeddedRedisConfig;
import umc.stockoneqback.common.TestBatchLegacyConfig;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;
import umc.stockoneqback.user.service.UserFindService;
import umc.stockoneqback.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.ANNE;

@Import({PassProductBatchConfig.class, TestBatchLegacyConfig.class, EmbeddedRedisConfig.class})
@SpringBootTest
@DisplayName("Product [Config Layer] -> PassProductBatchConfig 테스트")
public class PassProductBatchConfigTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFindService userFindService;

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    protected StoreRepository storeRepository;

    @Autowired
    protected ProductRepository productRepository;

    private final ProductFixture[] productFixtures = ProductFixture.values();
    private final Product[] products = new Product[17];
    private static final String FCM_TOKEN = "edwyeS_bHlpNcN-9play0t:APA91bHaigpNbcbNb2mng4Ho7vhbQYjIKnv_1AZTMx01tRN4CTKpadFTRYzSCfUFKPGfz1nCrZfTUxAYhuSHMNlty-1GEbZBCVdD451NDXuA-O8YwPj91MHtfy3XqdmLcKJICoYf47Dr";
    private static Long USER_ID;

    @BeforeEach
    void setup() {
        databaseCleaner.execute();
        Store zStore = storeRepository.save(Z_YEONGTONG.toStore());
        USER_ID = userService.saveManager(ANNE.toUser(), zStore.getId());
        authService.login(ANNE.getLoginId(), ANNE.getPassword());
        authService.saveFcm(USER_ID, FCM_TOKEN);
        for (int i = 0; i < products.length-1; i++)
            products[i] = productRepository.save(productFixtures[i].toProduct(zStore));
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
