package umc.stockoneqback.admin.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.common.EmbeddedRedisConfig;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataRedisTest
@Import(EmbeddedRedisConfig.class)
@DisplayName("Admin [Repository Layer] -> StaticFARedisRepository 테스트")
public class StaticFARedisRepositoryTest {
    @Autowired
    private StaticFARedisRepository StaticFARedisRepository;
    private final List<String> questionList = Arrays.asList(
                    "Q1. 슈퍼바이저가 다른 프랜차이즈로 이직할 경우 회원정보는 어떻게 변경하나요?",
                    "Q2. 알바생이랑 슈퍼바이저는 어떤 기능을 사용할 수 있나요?"
    );
    private final List<String> answerList = Arrays.asList(
                    "A1. 이직하는 프랜차이즈의 발급코드가 필요하기 때문에 탈퇴하고 다시 회원가입을 진행해주셔야 합니다.",
                    "A2. 알바생은 사장님과 동일하게 Home(재고 관리), Connect, 마이페이지 메뉴를 이용할 수 있고, " +
                            "슈퍼바이저는 Connect와 마이페이지 메뉴를 이용할 수 있습니다."
    );

    @BeforeEach
    void setup(){
        StaticFARedisRepository.deleteAll();
        for (int i = 0; i < questionList.size(); i++) {
            StaticFARedisRepository.save(StaticFA.builder()
                            .id(questionList.get(i))
                            .answer(answerList.get(i))
                            .build());
        }
    }

    @Test
    @DisplayName("FA 내용이 정상적으로 Redis에 저장되는지 확인한다")
    void isSaved() {
        String question = "Q3. 전 개인 카페 자영업자인데 Connect 서비스를 이용하지 못하는 것인가요?";
        String answer = "A3. 네, 아쉽지만 해당 슈퍼바이저와 연결되는 Connect 서비스를 이용할 수 없습니다. " +
                "하지만, 재고 관리 기능과 Community 기능을 이용해 카페 운영을 원활하게 할 수 있습니다.";
        StaticFA staticFA = StaticFA.builder()
                .id(question)
                .answer(answer)
                .build();

        StaticFARedisRepository.save(staticFA);

        StaticFA result = StaticFARedisRepository.findById(question).orElseThrow();
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(question),
                () -> assertThat(result.getAnswer()).isEqualTo(answer)
        );
    }

    @Test
    @DisplayName("FA 내용이 모두 List로 반환되는지 확인한다")
    void getFAListOfAll() {
        List<StaticFA> staticFAList = StaticFARedisRepository.findAll();

        assertAll(
                () -> assertThat(questionList).contains(staticFAList.get(0).getId()),
                () -> assertThat(answerList).contains(staticFAList.get(0).getAnswer()),
                () -> assertThat(questionList).contains(staticFAList.get(1).getId()),
                () -> assertThat(answerList).contains(staticFAList.get(1).getAnswer()),
                () -> assertThat(staticFAList.size()).isEqualTo(2)
        );
    }
}
