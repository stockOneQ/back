package umc.stockoneqback.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.admin.domain.StaticFA;
import umc.stockoneqback.admin.dto.request.AddFARequest;
import umc.stockoneqback.common.EmbeddedRedisConfig;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.UserFixture;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.exception.GlobalErrorCode;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(EmbeddedRedisConfig.class)
@DisplayName("Admin [Service Layer] -> AdminStaticService 테스트")
public class AdminStaticServiceTest extends ServiceTest {
    @Autowired
    private AdminStaticService adminStaticService;
    private final List<String> questionList = Arrays.asList(
            "Q1. 슈퍼바이저가 다른 프랜차이즈로 이직할 경우 회원정보는 어떻게 변경하나요?",
            "Q2. 알바생이랑 슈퍼바이저는 어떤 기능을 사용할 수 있나요?"
    );
    private final List<String> answerList = Arrays.asList(
            "A1. 이직하는 프랜차이즈의 발급코드가 필요하기 때문에 탈퇴하고 다시 회원가입을 진행해주셔야 합니다.",
            "A2. 알바생은 사장님과 동일하게 Home(재고 관리), Connect, 마이페이지 메뉴를 이용할 수 있고, " +
                    "슈퍼바이저는 Connect와 마이페이지 메뉴를 이용할 수 있습니다."
    );
    private static Long ADMIN_ID;

    @BeforeEach
    void setup(){
        ADMIN_ID = userRepository.save(UserFixture.ADMIN.toUser()).getId();
        staticFARedisRepository.deleteAll();
        for (int i = 0; i < questionList.size(); i++) {
            staticFARedisRepository.save(StaticFA.builder()
                    .id(questionList.get(i))
                    .answer(answerList.get(i))
                    .build());
        }
    }

    @Nested
    @DisplayName("공통 예외")
    class commonError {
        private final String QUESTION = "Q2. 알바생이랑 슈퍼바이저는 어떤 기능을 사용할 수 있나요?";

        @Test
        @DisplayName("권한이 없는 사용자가 Admin API를 호출한 경우 API 호출에 실패한다")
        void throwExceptionByUnauthorizedUser() {
            Long UNAUTHORIZED_ID = userRepository.save(UserFixture.WIZ.toUser()).getId();

            assertThatThrownBy(() -> adminStaticService.deleteFA(UNAUTHORIZED_ID, QUESTION))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(GlobalErrorCode.NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("F&A 추가")
    class addFA {
        private final String QUESTION = "Q3. 전 개인 카페 자영업자인데 Connect 서비스를 이용하지 못하는 것인가요?";
        private final String ANSWER = "A3. 네, 아쉽지만 해당 슈퍼바이저와 연결되는 Connect 서비스를 이용할 수 없습니다. " +
                "하지만, 재고 관리 기능과 Community 기능을 이용해 카페 운영을 원활하게 할 수 있습니다.";

        @Test
        @DisplayName("F&A 추가에 성공한다")
        void success() {
            AddFARequest addFARequest = new AddFARequest(List.of(new AddFARequest.AddFAKeyValue(QUESTION, ANSWER)));
            adminStaticService.addFA(ADMIN_ID, addFARequest);

            StaticFA staticFA = staticFARedisRepository.findById(QUESTION).orElseThrow();

            assertAll(
                    () -> assertThat(staticFA.getId()).isEqualTo(QUESTION),
                    () -> assertThat(staticFA.getAnswer()).isEqualTo(ANSWER)
            );
        }
    }

    @Nested
    @DisplayName("F&A 삭제")
    class deleteFA {
        private final String QUESTION = "Q2. 알바생이랑 슈퍼바이저는 어떤 기능을 사용할 수 있나요?";
        private final String ANSWER = "A2. 알바생은 사장님과 동일하게 Home(재고 관리), Connect, 마이페이지 메뉴를 이용할 수 있고, " +
                "슈퍼바이저는 Connect와 마이페이지 메뉴를 이용할 수 있습니다.";

        @Test
        @DisplayName("F&A 삭제에 성공한다")
        void success() {
            adminStaticService.deleteFA(ADMIN_ID, QUESTION);

            List<StaticFA> staticFAList = staticFARedisRepository.findAll();

            assertThat(staticFAList).doesNotContain(StaticFA.builder()
                                                            .id(QUESTION)
                                                            .answer(ANSWER)
                                                            .build());
        }
    }
}
