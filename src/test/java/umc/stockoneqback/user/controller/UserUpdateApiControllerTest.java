package umc.stockoneqback.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.controller.dto.request.UpdatePasswordRequest;
import umc.stockoneqback.user.controller.dto.request.UserInfoRequest;
import umc.stockoneqback.user.controller.dto.request.ValidateUpdatePasswordRequest;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.response.UpdatePasswordResponse;

import java.time.LocalDate;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("User [Controller Layer] -> UserUpdateApiController 테스트")
class UserUpdateApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("회원 정보 수정 API [PUT /api/user/update]")
    class updateInformation {
        private static final String BASE_URL = "/api/user/update";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 회원정보 수정에 실패한다")
        void withoutAccessToken() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.DUPLICATE_LOGIN_ID))
                    .when(userUpdateService)
                    .updateInformation(anyLong(), anyString(), any(), anyString(), anyString(), anyString(), anyString());

            // when
            final UserInfoRequest request = createUserInfoRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

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
                                    "UserApi/Update/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호")
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
        @DisplayName("중복된 로그인 아이디가 존재한다면 회원 정보 수정에 실패한다")
        void throwExceptionByDuplicateLoginId() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.DUPLICATE_LOGIN_ID))
                    .when(userUpdateService)
                    .updateInformation(anyLong(), anyString(), any(), anyString(), anyString(), anyString(), anyString());

            // when
            final UserInfoRequest request = createUserInfoRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final UserErrorCode expectedError = UserErrorCode.DUPLICATE_LOGIN_ID;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "UserApi/Update/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호")
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
        @DisplayName("회원 정보 수정에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(userUpdateService)
                    .updateInformation(anyLong(), anyString(), any(), anyString(), anyString(), anyString(), anyString());

            // when
            final UserInfoRequest request = createUserInfoRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/Update/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 가능 여부 검증 API [GET /api/user/update/password]")
    class validateUpdatePassword {
        private static final String BASE_URL = "/api/user/update/password";

        @Test
        @DisplayName("정보가 일치하는 사용자를 찾을 수 없으면 비밀번호 변경 가능 여부 검증에 실패한다")
        void throwExceptionByUserNotFound() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.USER_NOT_FOUND))
                    .when(userUpdateService)
                    .validateUpdatePassword(anyString(), any(), any());

            // when
            final ValidateUpdatePasswordRequest request = createPasswordUpdateRequest();
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
                                    "UserApi/validateUpdatePassword/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("loginId").description("로그인 아이디")
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
        @DisplayName("비밀번호 변경 가능 여부 검증에 성공한다")
        void success() throws Exception {
            // given
            doReturn(new UpdatePasswordResponse("gildong", TRUE))
                    .when(userUpdateService)
                    .validateUpdatePassword(anyString(), any(), anyString());

            // when
            final ValidateUpdatePasswordRequest request = createPasswordUpdateRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/validateUpdatePassword/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("loginId").description("로그인 아이디")
                                    ),
                                    responseFields(
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("validate").description("비밀번호 변경 검증 결과 [TRUE/FALSE]")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 API [PUT /api/user/update/password]")
    class updatePassword {
        private static final String BASE_URL = "/api/user/update/password";

        @Test
        @DisplayName("정보가 일치하는 사용자를 찾을 수 없으면 비밀번호 변경에 실패한다")
        void throwExceptionByUserNotFound() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.USER_NOT_FOUND))
                    .when(userUpdateService)
                    .updatePassword(anyString(), any(), any());

            // when
            final UpdatePasswordRequest request = new UpdatePasswordRequest(TRUE, "gildong", "securenew123!");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL)
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
                                    "UserApi/UpdatePassword/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("validate").description("비밀번호 변경 검증 결과 [TRUE/FALSE]"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("newPassword").description("변경할 비밀번호")
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
        @DisplayName("비밀번호 변경 검증이 되지 않았다면 비밀번호 변경에 실패한다")
        void throwExceptionByNotAllowedUpdatePassword() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.NOT_ALLOWED_UPDATE_PASSWORD))
                    .when(userUpdateService)
                    .updatePassword(anyString(), any(), any());

            // when
            final UpdatePasswordRequest request = new UpdatePasswordRequest(FALSE, "gildong", "securenew123!");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final UserErrorCode expectedError = UserErrorCode.NOT_ALLOWED_UPDATE_PASSWORD;
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
                                    "UserApi/UpdatePassword/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("validate").description("비밀번호 변경 검증 결과 [TRUE/FALSE]"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("newPassword").description("변경할 비밀번호")
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
        @DisplayName("비밀번호 변경에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(userUpdateService)
                    .updatePassword(anyString(), any(), any());

            // when
            final UpdatePasswordRequest request = new UpdatePasswordRequest(TRUE, "gildong", "securenew123!");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .put(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/UpdatePassword/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("validate").description("비밀번호 변경 검증 결과 [TRUE/FALSE]"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("newPassword").description("변경할 비밀번호")
                                    )
                            )
                    );
        }
    }

    private UserInfoRequest createUserInfoRequest() {
        return new UserInfoRequest(
                SAEWOO.getName(),
                SAEWOO.getBirth(),
                SAEWOO.getEmail(),
                SAEWOO.getLoginId(),
                SAEWOO.getPassword(),
                SAEWOO.getPhoneNumber()
        );
    }

    private ValidateUpdatePasswordRequest createPasswordUpdateRequest() {
        return new ValidateUpdatePasswordRequest("홍길동", LocalDate.now(), "gildong");
    }
}