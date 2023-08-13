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
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.infra.query.dto.FindManager;
import umc.stockoneqback.user.service.dto.response.FindManagerResponse;

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
    @DisplayName("친구 찾기(매니저 검색) API [GET /api/user/friend]")
    class findFriendManagers {
        private static final String BASE_URL = "/api/user/friend";
        private static final Long USER_ID = 1L;
        private static final Long LAST_USER_ID = -1L;
        private static final String SEARCH_TYPE = "이름";
        private static final String INVALID_SEARCH_TYPE = "전화번호";
        private static final String INVALID_SEARCH_WORD = "";
        private static final String SEARCH_WORD = "동";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 친구 찾기(매니저 검색)에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .param("last", String.valueOf(LAST_USER_ID));

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
                                    "UserApi/Find/Friend/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("search").description("검색 조건(이름/상호명/지역명)"),
                                            parameterWithName("word").description("검색어"),
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
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
        void throwExceptionByInvalidSearchType() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.INVALID_SEARCH_TYPE))
                    .when(userFindService)
                    .findFriendManagers(anyLong(), eq(LAST_USER_ID), eq(INVALID_SEARCH_TYPE), eq(SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("search", INVALID_SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.INVALID_SEARCH_TYPE;
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
                                    "UserApi/Find/Friend/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("search").description("검색 조건(이름/상호명/지역명)"),
                                            parameterWithName("word").description("검색어"),
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
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
        @DisplayName("검색어를 입력하지 않았다면 매니저 검색에 실패한다")
        void throwExceptionByInputValueRequired() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.INPUT_VALUE_REQUIRED))
                    .when(userFindService)
                    .findFriendManagers(anyLong(), eq(LAST_USER_ID), eq(SEARCH_TYPE), eq(INVALID_SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("search", SEARCH_TYPE)
                    .param("word", INVALID_SEARCH_WORD)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.INPUT_VALUE_REQUIRED;
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
                                    "UserApi/Find/Friend/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("search").description("검색 조건(이름/상호명/지역명)"),
                                            parameterWithName("word").description("검색어"),
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
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
        @DisplayName("친구 찾기(매니저 검색)에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getFindFriendManagerResponse())
                    .when(userFindService)
                    .findFriendManagers(USER_ID, LAST_USER_ID, SEARCH_TYPE, SEARCH_WORD);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/Find/Friend/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("search").description("검색 조건(이름/상호명/지역명)"),
                                            parameterWithName("word").description("검색어"),
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("searchedUser[].id").type(JsonFieldType.NUMBER).description("유저 ID"),
                                            fieldWithPath("searchedUser[].name").type(JsonFieldType.STRING).description("유저 이름"),
                                            fieldWithPath("searchedUser[].storeName").type(JsonFieldType.STRING).description("유저 가게 이름"),
                                            fieldWithPath("searchedUser[].phoneNumber").type(JsonFieldType.STRING).description("유저 연락처"),
                                            fieldWithPath("searchedUser[].relationStatus").type(JsonFieldType.STRING).description("해당 유저와의 친구 상태(친구 수락/친구 요청/친구 아님)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("점주 찾기(매니저 검색) API [GET /api/user/business]")
    class findBusinessManagers {
        private static final String BASE_URL = "/api/user/business";
        private static final Long USER_ID = 1L;
        private static final Long LAST_USER_ID = -1L;
        private static final String SEARCH_TYPE = "상호명";
        private static final String INVALID_SEARCH_TYPE = "지역명";
        private static final String INVALID_SEARCH_WORD = "";
        private static final String SEARCH_WORD = "동";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 점주 찾기(매니저 검색)에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .param("last", String.valueOf(LAST_USER_ID));

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
                                    "UserApi/Find/Business/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("search").description("검색 조건(이름/상호명)"),
                                            parameterWithName("word").description("검색어"),
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
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
        void throwExceptionByInvalidSearchType() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.INVALID_SEARCH_TYPE))
                    .when(userFindService)
                    .findBusinessManagers(anyLong(), eq(LAST_USER_ID), eq(INVALID_SEARCH_TYPE), eq(SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("search", INVALID_SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.INVALID_SEARCH_TYPE;
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
                                    "UserApi/Find/Business/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("search").description("검색 조건(이름/상호명)"),
                                            parameterWithName("word").description("검색어"),
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
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
        @DisplayName("검색어를 입력하지 않았다면 매니저 검색에 실패한다")
        void throwExceptionByInputValueRequired() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.INPUT_VALUE_REQUIRED))
                    .when(userFindService)
                    .findBusinessManagers(anyLong(), eq(LAST_USER_ID), eq(SEARCH_TYPE), eq(INVALID_SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("search", SEARCH_TYPE)
                    .param("word", INVALID_SEARCH_WORD)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.INPUT_VALUE_REQUIRED;
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
                                    "UserApi/Find/Business/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("search").description("검색 조건(이름/상호명)"),
                                            parameterWithName("word").description("검색어"),
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
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
        @DisplayName("점주 찾기(매니저 검색)에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getFindBusinessManagerResponse())
                    .when(userFindService)
                    .findBusinessManagers(USER_ID, LAST_USER_ID, SEARCH_TYPE, SEARCH_WORD);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/Find/Business/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("search").description("검색 조건(이름/상호명)"),
                                            parameterWithName("word").description("검색어"),
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("searchedUser[].id").type(JsonFieldType.NUMBER).description("유저 ID"),
                                            fieldWithPath("searchedUser[].name").type(JsonFieldType.STRING).description("유저 이름"),
                                            fieldWithPath("searchedUser[].storeName").type(JsonFieldType.STRING).description("유저 가게 이름"),
                                            fieldWithPath("searchedUser[].phoneNumber").type(JsonFieldType.STRING).description("유저 연락처"),
                                            fieldWithPath("searchedUser[].relationStatus").type(JsonFieldType.STRING).description("해당 유저와의 비즈니스 상태(정상/소멸)")
                                    )
                            )
                    );
        }
    }

    private FindManagerResponse getFindFriendManagerResponse() {
        return new FindManagerResponse(
                List.of(
                        new FindManager(2L, "강동원", "스타벅스 강동역점", "01012345678", "친구 수락"),
                        new FindManager(3L, "이동욱", "스타벅스 동대입구역점", "01098765432", "친구 요청"),
                        new FindManager(5L, "신동엽", "스타벅스 동성로점", "01013572468", "친구 아님")
                )
        );
    }

    private FindManagerResponse getFindBusinessManagerResponse() {
        return new FindManagerResponse(
                List.of(
                        new FindManager(2L, "강동원", "스타벅스 강동역점", "01012345678", "정상"),
                        new FindManager(3L, "이동욱", "스타벅스 동대입구역점", "01098765432", "정상"),
                        new FindManager(5L, "신동엽", "스타벅스 동성로점", "01013572468", "소멸")
                )
        );
    }
}