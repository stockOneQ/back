package umc.stockoneqback.product.config;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.auth.domain.FCMToken;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.auth.service.TokenService;
import umc.stockoneqback.common.EmbeddedRedisConfig;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.product.domain.Product;
import umc.stockoneqback.product.domain.ProductRepository;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;
import umc.stockoneqback.user.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
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
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFindService userFindService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    private final ProductFixture[] productFixtures = ProductFixture.values();
    private final Product[] products = new Product[17];
    private static final String FCM_TOKEN = "examplefcmtokenblabla";
    private static Long USER_ID;
    private static Store zStore;

    @BeforeEach
    void setup() {
        zStore = storeRepository.save(Z_YEONGTONG.toStore());
        USER_ID = userService.saveManager(ANNE.toUser(), zStore.getId());
        authService.login(ANNE.getLoginId(), ANNE.getPassword());
        authService.saveFcm(USER_ID, FCM_TOKEN);
        for (int i = 0; i < products.length-1; i++)
            products[i] = productRepository.save(productFixtures[i].toProduct(zStore));
    }

    @Nested
    @DisplayName("특정 시간마다 현재 접속중인 사용자별 유통기한 경과 제품 목록 조회")
    class getListSpecificTimeOfPassProductByOnlineUsers {
        @Test
        @DisplayName("특정 시간마다 현재 접속중인 사용자별 유통기한 경과 제품 목록 조회에 성공한다")
        void success() throws Exception {
            Awaitility.await()
                    .atMost(3, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        List<FCMToken> tokenList = tokenService.findAllOnlineUsers();
                        User user = userFindService.findById(tokenList.get(0).getId());
                        List<Product> getListOfPassProductByOnlineUsersResponse = productRepository.findPassByManager(user, LocalDate.now());

                        assertAll(
                                () -> assertThat(user.getId()).isEqualTo(USER_ID),
                                () -> assertThat(tokenList.get(0).getToken()).isEqualTo(FCM_TOKEN),
                                () -> assertThat(getListOfPassProductByOnlineUsersResponse.size()).isEqualTo(2),
                                () -> assertThat(getListOfPassProductByOnlineUsersResponse.get(0).getName()).isEqualTo("감"),
                                () -> assertThat(getListOfPassProductByOnlineUsersResponse.get(1).getName()).isEqualTo("포도")
                        );
                    }
            );
        }
    }
}
