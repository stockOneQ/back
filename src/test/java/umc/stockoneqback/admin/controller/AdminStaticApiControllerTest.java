package umc.stockoneqback.admin.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.admin.dto.request.AddFARequest;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.GlobalErrorCode;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;

@DisplayName("Admin [Controller Layer] -> AdminStaticApiController 테스트")
public class AdminStaticApiControllerTest extends ControllerTest {
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

    @Nested
    @DisplayName("공통 예외")
    class commonError {
        private static final String BASE_URL = "/api/admin/fa";

        @Test
        @DisplayName("권한이 없는 사용자가 Admin API를 호출한 경우 API 호출에 실패한다")
        void throwExceptionByUnauthorizedUser() throws Exception {
            // given
            doThrow(BaseException.type(GlobalErrorCode.NOT_FOUND))
                    .when(adminStaticService)
                    .deleteFA(anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL)
                    .param("question", questionList.get(0))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "AdminStaticApi/CommonError/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("question").description("삭제할 질문")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("F&A 추가 API [POST /api/admin/fa]")
    class addFA {
        private static final String BASE_URL = "/api/admin/fa";

        @Test
        @DisplayName("F&A 추가에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(adminStaticService)
                    .addFA(anyLong(), any());

            // when
            final AddFARequest addFARequest = createAddFARequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addFARequest))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "AdminStaticApi/AddFA/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("addFAKeyValueList[].question").description("질문"),
                                            fieldWithPath("addFAKeyValueList[].answer").description("답변")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("F&A 삭제 API [DELETE /api/admin/fa]")
    class deleteFA {
        private static final String BASE_URL = "/api/admin/fa";

        @Test
        @DisplayName("F&A 삭제에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(adminStaticService)
                    .deleteFA(anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL)
                    .param("question", questionList.get(0))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "AdminStaticApi/DeleteFA/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("question").description("삭제할 질문")
                                    )
                            )
                    );
        }
    }

    private AddFARequest createAddFARequest() {
        return new AddFARequest(List.of(new AddFARequest.AddFAKeyValue(questionList.get(0), answerList.get(0)),
                new AddFARequest.AddFAKeyValue(questionList.get(1), answerList.get(1)),
                new AddFARequest.AddFAKeyValue(questionList.get(2), answerList.get(2))));
    }
}
