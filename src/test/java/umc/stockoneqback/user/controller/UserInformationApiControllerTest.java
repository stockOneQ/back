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
import umc.stockoneqback.user.controller.dto.request.FindLoginIdRequest;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.response.LoginIdResponse;
import umc.stockoneqback.user.service.dto.response.UserInformationResponse;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.common.DocumentFormatGenerator.getDateFormat;
import static umc.stockoneqback.fixture.StoreFixture.A_PASTA;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("User [Controller Layer] -> UserInformationApiController 테스트")
class UserInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("아이디 찾기 API [GET /api/user/find-id]")
    class findLoginId {
        private static final String BASE_URL = "/api/user/find-id";

        @Test
        @DisplayName("요청된 정보와 일치하는 사용자를 찾을 수 없으면 아이디 찾기에 실패한다")
        void throwExceptionByUserNotFound() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.USER_NOT_FOUND))
                    .when(userInformationService)
                    .findLoginId(anyString(), any(), any());

            // when
            final FindLoginIdRequest request = createFindLoginIdRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final UserErrorCode expectedError = UserErrorCode.USER_NOT_FOUND;
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
                                    "UserApi/Information/FindLoginId/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일")
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
        @DisplayName("아이디 찾기에 성공한다")
        void success() throws Exception {
            // given
            doReturn(new LoginIdResponse("gildong"))
                    .when(userInformationService)
                    .findLoginId(any(), any(), any());

            // when
            final FindLoginIdRequest request = createFindLoginIdRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/Information/FindLoginId/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일")
                                    ),
                                    responseFields(
                                            fieldWithPath("loginId").description("로그인 아이디")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자 정보 조회 API [GET /api/user/information]")
    class getInformation {
        private static final String BASE_URL = "/api/user/information";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 사용자 정보 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL);

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
                                    "UserApi/Information/Get/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("사용자를 찾을 수 없으면 사용자 정보 조회에 실패한다")
        void throwExceptionByUserNotFound() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.USER_NOT_FOUND))
                    .when(userInformationService)
                    .getInformation(any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.USER_NOT_FOUND;
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
                                    "UserApi/Information/Get/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
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
        @DisplayName("사용자 정보 조회에 성공한다 - 사장님")
        void successManager() throws Exception {
            // given
            UserInformationResponse response = new UserInformationResponse(1L, MIKE.getEmail(), MIKE.getLoginId(), MIKE.name(), MIKE.getBirth(),
                    MIKE.getPhoneNumber(), MIKE.getRole().getValue(), A_PASTA.name(), A_PASTA.getCode(), A_PASTA.getAddress(), null);
            doReturn(response)
                    .when(userInformationService)
                    .getInformation(any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/Information/Get/Success/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("사용자 ID (PK)"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").attributes(getDateFormat()).description("생년월일"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("role").description("역할"),
                                            fieldWithPath("storeName").description("가게 이름"),
                                            fieldWithPath("storeCode").description("가게 코드"),
                                            fieldWithPath("storeAddress").description("가게 주소"),
                                            fieldWithPath("companyName").description("회사 이름")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("사용자 정보 조회에 성공한다 - 아르바이트생")
        void successPartTimer() throws Exception {
            // given
            UserInformationResponse response = new UserInformationResponse(1L, SAEWOO.getEmail(), SAEWOO.getLoginId(), SAEWOO.name(), SAEWOO.getBirth(),
                    SAEWOO.getPhoneNumber(), SAEWOO.getRole().getValue(), A_PASTA.name(), null, A_PASTA.getAddress(), null);
            doReturn(response)
                    .when(userInformationService)
                    .getInformation(any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/Information/Get/Success/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("사용자 ID (PK)"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").attributes(getDateFormat()).description("생년월일"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("role").description("역할"),
                                            fieldWithPath("storeName").description("가게 이름"),
                                            fieldWithPath("storeCode").description("가게 코드"),
                                            fieldWithPath("storeAddress").description("가게 주소"),
                                            fieldWithPath("companyName").description("회사 이름")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("사용자 정보 조회에 성공한다 - 슈퍼바이저")
        void successSupervisor() throws Exception {
            // given
            UserInformationResponse response = new UserInformationResponse(1L, JACK.getEmail(), JACK.getLoginId(), JACK.name(), JACK.getBirth(),
                    JACK.getPhoneNumber(), JACK.getRole().getValue(), null, null, null, "투썸플레이스 본사");
            doReturn(response)
                    .when(userInformationService)
                    .getInformation(any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/Information/Get/Success/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("사용자 ID (PK)"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").attributes(getDateFormat()).description("생년월일"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("role").description("역할"),
                                            fieldWithPath("storeName").description("가게 이름"),
                                            fieldWithPath("storeCode").description("가게 코드"),
                                            fieldWithPath("storeAddress").description("가게 주소"),
                                            fieldWithPath("companyName").description("회사 이름")
                                    )
                            )
                    );
        }
    }

    private FindLoginIdRequest createFindLoginIdRequest() {
        return new FindLoginIdRequest(
                "홍길동",
                LocalDate.now(),
                "gildong@gmail.com"
        );
    }
}