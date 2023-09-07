package umc.stockoneqback.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import umc.stockoneqback.auth.controller.dto.request.SaveFcmRequest;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.auth.service.dto.response.TokenResponse;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.exception.BaseException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.TokenFixture.*;

@DisplayName("Auth [Controller Layer] -> TokenReissueApiController 테스트")
class RefreshTokenReissueApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("토큰 재발급 API 테스트 [POST /api/token/reissue]")
    class reissueTokens {
        private static final String BASE_URL = "/api/token/reissue";

        @Test
        @DisplayName("Authorization_Header에 RefreshToken이 없으면 예외가 발생한다")
        void throwExceptionByInvalidPermission() throws Exception {
            // when
            final SaveFcmRequest request = createSaveFcmRequest();
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
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
                                    "TokenReissueApi/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("fcmToken").description("현재 사용자의 FCM Token")
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
        @DisplayName("만료된 RefreshToken으로 인해 토큰 재발급에 실패한다")
        void throwExceptionByAuthExpiredToken() throws Exception {
            // given
            given(jwtTokenProvider.getId(anyString()))
                    .willThrow(BaseException.type(AuthErrorCode.AUTH_EXPIRED_TOKEN));

            // when
            final SaveFcmRequest request = createSaveFcmRequest();
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + REFRESH_TOKEN);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.AUTH_EXPIRED_TOKEN;
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
                                    "TokenReissueApi/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Refresh Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("fcmToken").description("현재 사용자의 FCM Token")
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
        @DisplayName("이미 사용한 RefreshToken이거나 조작된 RefreshToken이면 재발급에 실패한다")
        void throwExceptionByAuthInvalidToken() throws Exception {
            // given
            given(jwtTokenProvider.getId(anyString()))
                    .willThrow(BaseException.type(AuthErrorCode.AUTH_INVALID_TOKEN));

            // when
            final SaveFcmRequest request = createSaveFcmRequest();
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + REFRESH_TOKEN);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.AUTH_INVALID_TOKEN;
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
                                    "TokenReissueApi/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Refresh Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("fcmToken").description("현재 사용자의 FCM Token")
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
        @DisplayName("RefreshToken으로 AccessToken과 RefreshToken을 재발급받는다.")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getId(REFRESH_TOKEN)).willReturn(1L);

            TokenResponse response = new TokenResponse(ACCESS_TOKEN, REFRESH_TOKEN);
            given(tokenReissueService.reissueTokens(1L, REFRESH_TOKEN, FCM_TOKEN)).willReturn(response);

            // when
            final SaveFcmRequest request = createSaveFcmRequest();
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + REFRESH_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.accessToken").exists(),
                            jsonPath("$.accessToken").value(ACCESS_TOKEN),
                            jsonPath("$.refreshToken").exists(),
                            jsonPath("$.refreshToken").value(REFRESH_TOKEN)
                    )
                    .andDo(
                            document(
                                    "TokenReissueApi/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Refresh Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("fcmToken").description("현재 사용자의 FCM Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("accessToken").description("새로 발급된 Access Token (Expire - 2시간)"),
                                            fieldWithPath("refreshToken").description("새로 발급된 Refresh Token (Expire - 2주)")
                                    )
                            )
                    );
        }
    }

    private SaveFcmRequest createSaveFcmRequest() {
        return new SaveFcmRequest(FCM_TOKEN);
    }
}
