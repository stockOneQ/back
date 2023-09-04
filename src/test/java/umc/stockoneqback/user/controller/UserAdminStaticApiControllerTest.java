package umc.stockoneqback.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.user.service.dto.response.GetFAResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;

@DisplayName("User [Controller Layer] -> UserFAApiController 테스트")
public class UserAdminStaticApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 F&A 조회 API [GET /api/user/fa]")
    class getFAList {
        private static final String BASE_URL = "/api/user/fa";
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

        @Test
        @DisplayName("사용자 F&A 조회에 성공한다")
        void success() throws Exception {
            // given
            doReturn(getFAResponse())
                    .when(userFAService)
                    .getFA();

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result[0]").exists(),
                            jsonPath("$.result[0].question").value(questionList.get(0)),
                            jsonPath("$.result[0].answer").value(answerList.get(0)),
                            jsonPath("$.result[1]").exists(),
                            jsonPath("$.result[1].question").value(questionList.get(1)),
                            jsonPath("$.result[1].answer").value(answerList.get(1)),
                            jsonPath("$.result[2]").exists(),
                            jsonPath("$.result[2].question").value(questionList.get(2)),
                            jsonPath("$.result[2].answer").value(answerList.get(2)),
                            jsonPath("$.result[3]").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "UserFAApi/GetFAList/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지"),
                                            fieldWithPath("result[].question").type(JsonFieldType.STRING).description("질문"),
                                            fieldWithPath("result[].answer").type(JsonFieldType.STRING).description("답변")
                                    )
                            )
                    );
        }

        private List<GetFAResponse> getFAResponse() {
            List<GetFAResponse> getFAResponseList = new ArrayList<>();
            getFAResponseList.add(new GetFAResponse("Q1. 슈퍼바이저가 다른 프랜차이즈로 이직할 경우 회원정보는 어떻게 변경하나요?",
                    "A1. 이직하는 프랜차이즈의 발급코드가 필요하기 때문에 탈퇴하고 다시 회원가입을 진행해주셔야 합니다."));
            getFAResponseList.add(new GetFAResponse("Q2. 알바생이랑 슈퍼바이저는 어떤 기능을 사용할 수 있나요?",
                    "A2. 알바생은 사장님과 동일하게 Home(재고 관리), Connect, 마이페이지 메뉴를 이용할 수 있고, " +
                            "슈퍼바이저는 Connect와 마이페이지 메뉴를 이용할 수 있습니다."));
            getFAResponseList.add(new GetFAResponse("Q3. 전 개인 카페 자영업자인데 Connect 서비스를 이용하지 못하는 것인가요?",
                    "A3. 네, 아쉽지만 해당 슈퍼바이저와 연결되는 Connect 서비스를 이용할 수 없습니다. " +
                            "하지만, 재고 관리 기능과 Community 기능을 이용해 카페 운영을 원활하게 할 수 있습니다."));
            return getFAResponseList;
        }
    }
}
