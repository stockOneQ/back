package umc.stockoneqback.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.controller.dto.request.FindLoginIdRequest;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.response.LoginIdResponse;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                                    "UserApi/FindLoginId/Failure/Case1",
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
                                    "UserApi/FindLoginId/Success",
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

    private FindLoginIdRequest createFindLoginIdRequest() {
        return new FindLoginIdRequest(
                "홍길동",
                LocalDate.now(),
                "gildong@gmail.com"
        );
    }
}