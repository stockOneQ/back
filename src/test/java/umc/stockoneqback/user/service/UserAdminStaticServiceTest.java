package umc.stockoneqback.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.admin.domain.StaticFA;
import umc.stockoneqback.common.EmbeddedRedisConfig;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.user.service.dto.response.GetFAResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(EmbeddedRedisConfig.class)
@DisplayName("User [Service Layer] -> UserFAService 테스트")
public class UserAdminStaticServiceTest extends ServiceTest {
    @Autowired
    private UserFAService userFAService;
    private final List<String> questionList = Arrays.asList(
            "Q1. 슈퍼바이저가 다른 프랜차이즈로 이직할 경우 회원정보는 어떻게 변경하나요?",
            "Q2. 알바생이랑 슈퍼바이저는 어떤 기능을 사용할 수 있나요?",
            "Q3. 전 개인 카페 자영업자인데 Connect 서비스를 이용하지 못하는 것인가요?"
    );
    private final List<String> answerList = Arrays.asList(
            "A1. 이직하는 프랜차이즈의 발급코드가 필요하기 때문에 탈퇴하고 다시 회원가입을 진행해주셔야 합니다.",
            "A2. 알바생은 사장님과 동일하게 Home(재고 관리), Connect, 마이페이지 메뉴를 이용할 수 있고, " +
                    "슈퍼바이저는 Connect와 마이페이지 메뉴를 이용할 수 있습니다.",
            "A3. 네, 아쉽지만 해당 슈퍼바이저와 연결되는 Connect 서비스를 이용할 수 없습니다. " +
                    "하지만, 재고 관리 기능과 Community 기능을 이용해 카페 운영을 원활하게 할 수 있습니다."
    );

    @BeforeEach
    void setup() {
        for (int i = 0; i < questionList.size(); i++) {
            StaticFARedisRepository.save(StaticFA.builder()
                    .id(questionList.get(i))
                    .answer(answerList.get(i))
                    .build());
        }
    }

    @Nested
    @DisplayName("F&A 게시판 조회")
    class getFAList {
        @Test
        @DisplayName("F&A 게시판 조회에 성공한다")
        void success() {
            List<GetFAResponse> getFAResponseList = userFAService.getFA();

            assertAll(
                    () -> assertThat(questionList).contains(getFAResponseList.get(0).question()),
                    () -> assertThat(answerList).contains(getFAResponseList.get(0).answer()),
                    () -> assertThat(questionList).contains(getFAResponseList.get(1).question()),
                    () -> assertThat(answerList).contains(getFAResponseList.get(1).answer()),
                    () -> assertThat(questionList).contains(getFAResponseList.get(2).question()),
                    () -> assertThat(answerList).contains(getFAResponseList.get(2).answer()),
                    () -> assertThat(getFAResponseList.size()).isEqualTo(3)
            );
        }
    }
}
