package umc.stockoneqback.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.auth.controller.dto.request.LoginRequest;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.auth.service.dto.response.LoginResponse;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.TokenFixture.*;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("Auth [Controller Layer] -> AuthApiController 테스트")
class AuthApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("로그인 API [POST /api/auth/login]")
    class login {
        private static final String BASE_URL = "/api/auth/login";

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 로그인에 실패한다")
        void throwExceptionByWrongPassword() throws Exception {
            // given
            doThrow(BaseException.type(AuthErrorCode.WRONG_PASSWORD))
                    .when(authService)
                    .login(any(), any());

            // when
            final LoginRequest request = createLoginRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf());

            // then
            final AuthErrorCode expectedError = AuthErrorCode.WRONG_PASSWORD;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "AuthApi/Login/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("password").description("비밀번호")
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
        @DisplayName("로그인에 성공한다")
        void success() throws Exception {
            // given
            LoginResponse loginResponse = createLoginResponse();
            given(authService.login(any(), any())).willReturn(loginResponse);

            // when
            final LoginRequest request = createLoginRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "AuthApi/Login/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("password").description("비밀번호")
                                    ),
                                    responseFields(
                                            fieldWithPath("userId").description("로그인한 사용자의 ID (PK)"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("accessToken").description("발급된 Access Token"),
                                            fieldWithPath("refreshToken").description("발급된 Refresh Token")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("로그아웃 API [POST /api/auth/logout]")
    class logout {
        private static final String BASE_URL = "/api/auth/logout";
        private static final Long USER_ID = 1L;

        @Test
        @DisplayName("로그아웃에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            given(userFindService.findById(any())).willReturn(User.createUser(Email.from(SAEWOO.getEmail()), SAEWOO.getLoginId(), Password.encrypt(SAEWOO.getPassword(), ENCODER), SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getPhoneNumber(), SAEWOO.getRole()));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + REFRESH_TOKEN)
                    .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNoContent()
                    )
                    .andDo(
                            document(
                                    "AuthApi/Logout/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    )
                            )
                    );
        }
    }

    private LoginRequest createLoginRequest() {
        return new LoginRequest(SAEWOO.getEmail(), SAEWOO.getPassword());
    }

    private LoginResponse createLoginResponse() {
        return new LoginResponse(
                1L,
                SAEWOO.getLoginId(),
                ACCESS_TOKEN,
                REFRESH_TOKEN);
    }
}