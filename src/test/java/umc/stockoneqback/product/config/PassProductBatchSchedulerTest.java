package umc.stockoneqback.product.config;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.auth.domain.FcmToken;
import umc.stockoneqback.auth.domain.FcmTokenRedisRepository;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.common.DatabaseCleaner;
import umc.stockoneqback.common.EmbeddedRedisConfig;
import umc.stockoneqback.common.RedisCleaner;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.product.service.ProductService;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.service.UserFindService;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_SIHEUNG;
import static umc.stockoneqback.fixture.UserFixture.ANNE;

@Import(EmbeddedRedisConfig.class)
@DisplayName("Product [Config Layer] -> PassProductBatchScheduler 테스트")
@SpringBootTest(
        properties = {
                "schedules.cron.reward.publish=0/2 * * * * ?",
        }
)
public class PassProductBatchSchedulerTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private RedisCleaner redisCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FcmTokenRedisRepository fcmTokenRedisRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserFindService userFindService;

    @Autowired
    private ProductService productService;

    private final ProductFixture[] productFixtures = ProductFixture.values();

    private static final String FCM_TOKEN = "examplefcmtokenblabla";

    private static User user;
    private static Long storeId;

    @BeforeEach
    void setup() {
        databaseCleaner.flushAndClear();
        redisCleaner.flushAll();

        user = userRepository.save(ANNE.toUser());
        storeId = storeRepository.save(Store.createStore(Z_SIHEUNG.getName(), Z_SIHEUNG.getSector(), Z_SIHEUNG.getAddress(), user)).getId();
        userRepository.updateManagerStoreIdById(user.getId(), storeId);

        authService.login(ANNE.getLoginId(), ANNE.getPassword());
        authService.saveFcm(user.getId(), FCM_TOKEN);

        Store store = storeRepository.findById(storeId).orElseThrow();
        for (int i = 0; i < productFixtures.length; i++)
            productRepository.save(productFixtures[i].toProduct(store));
    }

    @AfterEach
    void clearDatabase() {
        databaseCleaner.flushAndClear();
        redisCleaner.flushAll();
    }

    @Nested
    @DisplayName("특정 시간마다 현재 접속중인 사용자별 유통기한 경과 제품 목록 조회")
    class getListSpecificTimeOfPassProductByOnlineUsers {
        @Test
        @DisplayName("특정 시간마다 현재 접속중인 사용자별 유통기한 경과 제품 목록 조회에 성공한다")
        void success() {
            Awaitility.await()
                    .atMost(3, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                                FcmToken fcmToken = fcmTokenRedisRepository.findById(user.getId()).orElseThrow();
                                User findUser = userFindService.findById(user.getId());
                                List<Product> getListOfPassProductByOnlineUsersResponse = productRepository.findPassByManager(findUser, LocalDate.now());

                                assertAll(
                                        () -> assertThat(findUser.getName()).isEqualTo(ANNE.getName()),
                                        () -> assertThat(findUser.getManagerStore().getId()).isEqualTo(storeId),
                                        () -> assertThat(fcmToken.getToken()).isEqualTo(FCM_TOKEN),
                                        () -> assertThat(getListOfPassProductByOnlineUsersResponse.size()).isEqualTo(2),
                                        () -> assertThat(getListOfPassProductByOnlineUsersResponse.get(0).getName()).isEqualTo("감"),
                                        () -> assertThat(getListOfPassProductByOnlineUsersResponse.get(1).getName()).isEqualTo("포도")
                                );
                            }
                    );
        }
    }
}
