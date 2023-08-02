package umc.stockoneqback.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.controller.dto.response.FindManagerResponse;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.infra.query.dto.FindManager;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;

@DisplayName("User [Controller Layer] -> UserFindApiController 테스트")
class UserFindApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("친구 찾기(매니저 검색) API [GET /api/user/search/manager]")
    class searchManager {
        private static final String BASE_URL = "/api/user/search/manager";
        private static final Long USER_ID = 1L;
        private static final Long LAST_USER_ID = -1L;
        private static final String SEARCH_CONDITION = "이름";
        private static final String INVALID_CONDITION = "전화번호";
        private static final String SEARCH_NAME = "동";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 요청이 들어온 친구 리스트 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("condition", SEARCH_CONDITION)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("name", SEARCH_NAME);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "UserApi/Find/SearchManager/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("condition").description("검색 조건(이름/상호명/지역명)"),
                                            parameterWithName("last").description("마지막으로 조회된 매니저 ID"),
                                            parameterWithName("name").description("검색 할 단어")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("유효하지 않은 검색 조건이라면 매니저 검색에 실패한다")
        void throwExceptionByInvalidSearchCondition() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.INVALID_SEARCH_CONDITION))
                    .when(userFindService)
                    .findManager(anyLong(), eq(INVALID_CONDITION), eq(LAST_USER_ID), eq(SEARCH_NAME));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("condition", INVALID_CONDITION)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("name", SEARCH_NAME)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.INVALID_SEARCH_CONDITION;
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
                                    "UserApi/Find/SearchManager/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("condition").description("검색 조건(이름/상호명/지역명)"),
                                            parameterWithName("last").description("마지막으로 조회된 매니저 ID"),
                                            parameterWithName("name").description("검색 할 단어")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("친구 찾기를 통한 매니저 검색에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getFindManagerResponse())
                    .when(userFindService)
                    .findManager(USER_ID, SEARCH_CONDITION, LAST_USER_ID, SEARCH_NAME);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("condition", SEARCH_CONDITION)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("name", SEARCH_NAME)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);;

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/Find/SearchManager/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("condition").description("검색 조건(이름/상호명/지역명)"),
                                            parameterWithName("last").description("마지막으로 조회된 매니저 ID"),
                                            parameterWithName("name").description("검색 할 단어")
                                    ),
                                    responseFields(
                                            fieldWithPath("searchedUser[].id").type(JsonFieldType.NUMBER).description("친구 ID"),
                                            fieldWithPath("searchedUser[].name").type(JsonFieldType.STRING).description("친구 이름"),
                                            fieldWithPath("searchedUser[].storeName").type(JsonFieldType.STRING).description("친구 가게 이름"),
                                            fieldWithPath("searchedUser[].phoneNumber").type(JsonFieldType.STRING).description("친구 연락처")
                                    )
                            )
                    );
        }
    }

    private FindManagerResponse getFindManagerResponse() {
        return new FindManagerResponse(
                List.of(
                        new FindManager(2L, "강동원", "스타벅스 강동역점", "01012345678"),
                        new FindManager(3L, "이동욱", "스타벅스 동대입구역점", "01098765432")
                )
        );
    }
}