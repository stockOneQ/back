package umc.stockoneqback.business.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.business.exception.BusinessErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@DisplayName("Business [Controller Layer] -> BusinessApiController 테스트")
class BusinessApiControllerTest extends ControllerTest {

    @Nested
    @DisplayName("슈퍼바이저 - 점주 관계 등록 API [POST /api/business/{managerId}]")
    class register {
        private static final String BASE_URL = "/api/business/{managerId}";
        private static final Long SUPERVISOR_ID = 1L;
        private static final Long MANAGER_ID = 1L;

        @Test
        @DisplayName("이미 존재하는 관계라면 등록에 실패한다")
        void throwExceptionByAlreadyExistBusiness() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(SUPERVISOR_ID);
            given(userFindService.findById(any())).willReturn(User.createUser(Email.from(SAEWOO.getEmail()), SAEWOO.getLoginId(), Password.encrypt(SAEWOO.getPassword(), ENCODER), SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getPhoneNumber(), SAEWOO.getRole()));
            doThrow(BaseException.type(BusinessErrorCode.ALREADY_EXIST_BUSINESS))
                    .when(businessService)
                    .register(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, MANAGER_ID)
                    .header(AUTHORIZATION, "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzg1NDIwMjR9.doqGa5Hcq6chjER1y5brJEv81z0njcJqeYxJb159ZX4")
                    .contentType(APPLICATION_JSON)
                    .with(csrf());

            // then
            final BusinessErrorCode expectedError = BusinessErrorCode.ALREADY_EXIST_BUSINESS;
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
                                    "BusinessApi/Register/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("managerId").description("슈퍼바이저가 연결 신청한 사장님 ID(PK)")
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
        @DisplayName("슈퍼바이저 - 점주 관계 등록에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(SUPERVISOR_ID);
            given(userFindService.findById(any())).willReturn(User.createUser(Email.from(SAEWOO.getEmail()), SAEWOO.getLoginId(), Password.encrypt(SAEWOO.getPassword(), ENCODER), SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getPhoneNumber(), SAEWOO.getRole()));
            doNothing()
                    .when(businessService)
                    .register(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, MANAGER_ID)
                    .header(AUTHORIZATION, "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzg1NDIwMjR9.doqGa5Hcq6chjER1y5brJEv81z0njcJqeYxJb159ZX4")
                    .contentType(APPLICATION_JSON)
                    .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "BusinessApi/Register/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("managerId").description("슈퍼바이저가 연결 신청한 사장님 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("슈퍼바이저 - 점주 관계 취소 API [DELETE /api/business/{managerId}]")
    class cancel {
        private static final String BASE_URL = "/api/business/{managerId}";
        private static final Long SUPERVISOR_ID = 1L;
        private static final Long MANAGER_ID = 1L;

        @Test
        @DisplayName("존재하지 않는 관계라면 취소에 실패한다")
        void throwExceptionByAlreadyExistBusiness() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(SUPERVISOR_ID);
            given(userFindService.findById(any())).willReturn(User.createUser(Email.from(SAEWOO.getEmail()), SAEWOO.getLoginId(), Password.encrypt(SAEWOO.getPassword(), ENCODER), SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getPhoneNumber(), SAEWOO.getRole()));
            doThrow(BaseException.type(BusinessErrorCode.BUSINESS_NOT_FOUND))
                    .when(businessService)
                    .cancel(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, MANAGER_ID)
                    .header(AUTHORIZATION, "Bearer " + createToken(MANAGER_ID))
                    .contentType(APPLICATION_JSON)
                    .with(csrf());

            // then
            final BusinessErrorCode expectedError = BusinessErrorCode.BUSINESS_NOT_FOUND;
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
                                    "BusinessApi/Cancel/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("managerId").description("슈퍼바이저가 연결 취소할 사장님 ID(PK)")
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
        @DisplayName("슈퍼바이저 - 점주 관계 등록에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(SUPERVISOR_ID);
            given(userFindService.findById(any())).willReturn(User.createUser(Email.from(SAEWOO.getEmail()), SAEWOO.getLoginId(), Password.encrypt(SAEWOO.getPassword(), ENCODER), SAEWOO.getName(), SAEWOO.getBirth(), SAEWOO.getPhoneNumber(), SAEWOO.getRole()));
            doNothing()
                    .when(businessService)
                    .register(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, MANAGER_ID)
                    .header(AUTHORIZATION, "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzg1NDIwMjR9.doqGa5Hcq6chjER1y5brJEv81z0njcJqeYxJb159ZX4")
                    .contentType(APPLICATION_JSON)
                    .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "BusinessApi/Register/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("managerId").description("슈퍼바이저가 연결 취소할 사장님 ID(PK)")
                                    )
                            )
                    );
        }
    }

    private String createToken(Long userId) {
        return jwtTokenProvider.createToken(userId);
    }
}