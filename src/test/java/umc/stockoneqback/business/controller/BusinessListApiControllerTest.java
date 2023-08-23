package umc.stockoneqback.business.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.business.infra.query.dto.BusinessList;
import umc.stockoneqback.business.service.dto.BusinessListResponse;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.time.LocalDateTime;
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

@DisplayName("Business [Controller Layer] -> BusinessListApiController 테스트")
class BusinessListApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("연결된 슈퍼바이저 목록 조회 API [GET /api/business/supervisors]")
    class getSupervisors {
        private static final String BASE_URL = "/api/business/supervisors";
        private static final Long USER_ID = 1L;
        private static final Long LAST_USER_ID = -1L;
        private static final String SEARCH = "길동";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 연결된 슈퍼바이저 목록 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("search", SEARCH);

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
                                    "BusinessApi/List/Supervisors/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID"),
                                            parameterWithName("search").description("검색어")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("매니저가 아니라면 연결된 슈퍼바이저 목록 조회에 실패한다")
        void throwExceptionByUserNotAllowed() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.USER_NOT_ALLOWED))
                    .when(businessListService)
                    .getSupervisors(anyLong(), eq(LAST_USER_ID), eq(SEARCH));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("search", SEARCH)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.USER_NOT_ALLOWED;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "BusinessApi/List/Supervisors/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID"),
                                            parameterWithName("search").description("검색어")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("연결된 슈퍼바이저 목록 조회에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getBusinessListResponse())
                    .when(businessListService)
                    .getSupervisors(USER_ID, LAST_USER_ID, SEARCH);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("search", SEARCH)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "BusinessApi/List/Supervisors/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID"),
                                            parameterWithName("search").description("검색어")
                                    ),
                                    responseFields(
                                            fieldWithPath("userList[].id").description("유저 id"),
                                            fieldWithPath("userList[].name").description("유저 이름"),
                                            fieldWithPath("userList[].storeCoName").description("가게 or 회사 이름"),
                                            fieldWithPath("userList[].phoneNumber").description("유저 연락처"),
                                            fieldWithPath("userList[].lastModifiedDate").description("마지막 수정 시간")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("연결된 점주 목록 조회 API [GET /api/business/managers]")
    class getManagers {
        private static final String BASE_URL = "/api/business/managers";
        private static final Long USER_ID = 1L;
        private static final Long LAST_USER_ID = -1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 연결된 점주 목록 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
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
                                    "BusinessApi/List/Managers/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("슈퍼바이저가 아니라면 연결된 점주 목록 조회에 실패한다")
        void throwExceptionByUserNotAllowed() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.USER_NOT_ALLOWED))
                    .when(businessListService)
                    .getManagers(anyLong(), eq(LAST_USER_ID));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.USER_NOT_ALLOWED;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "BusinessApi/List/Managers/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("연결된 점주 목록 조회에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getBusinessListResponse())
                    .when(businessListService)
                    .getManagers(USER_ID, LAST_USER_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "BusinessApi/List/Supervisor/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 유저 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("userList[].id").description("유저 id"),
                                            fieldWithPath("userList[].name").description("유저 이름"),
                                            fieldWithPath("userList[].storeCoName").description("가게 or 회사 이름"),
                                            fieldWithPath("userList[].phoneNumber").description("유저 연락처"),
                                            fieldWithPath("userList[].lastModifiedDate").description("마지막 수정 시간")
                                    )
                            )
                    );
        }
    }

    private BusinessListResponse getBusinessListResponse() {
        return new BusinessListResponse(
                List.of(
                        new BusinessList(1L, "홍길동", "공차 강남역점", "01011112222", LocalDateTime.now()),
                        new BusinessList(2L, "김길동", "할리스 강남역점", "01011113333", LocalDateTime.now()),
                        new BusinessList(3L, "이길동", "스타벅스 강남역점", "01011114444", LocalDateTime.now())
                )
        );
    }
}