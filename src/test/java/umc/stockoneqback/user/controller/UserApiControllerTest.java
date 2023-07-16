package umc.stockoneqback.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.exception.ApplicationException;
import umc.stockoneqback.role.exception.StoreErrorCode;
import umc.stockoneqback.user.controller.dto.request.SignUpManagerRequest;
import umc.stockoneqback.user.controller.dto.request.SignUpPartTimerRequest;
import umc.stockoneqback.user.controller.dto.request.SignUpSupervisorRequest;
import umc.stockoneqback.user.exception.UserErrorCode;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("User [Controller Layer] -> UserApiController 테스트")
class UserApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("가게 사장님 등록 API [POST /api/user/manager]")
    class signUpManager {
        private static final String BASE_URL = "/api/user/manager";
        private static final Long STORE_ID = 1L;
        private static final Long USER_ID = 1L;

        @Test
        @DisplayName("중복된 가게 이름이 존재한다면 가게 사장님 등록에 실패한다")
        void throwExceptionByAlreadyExistStore() throws Exception {
            // given
            doThrow(ApplicationException.type(StoreErrorCode.ALREADY_EXIST_STORE))
                    .when(storeService)
                    .save(any(), any(), any());

            // when
            final SignUpManagerRequest request = createSignUpManagerRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final StoreErrorCode expectedError = StoreErrorCode.ALREADY_EXIST_STORE;
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
                                    "UserApi/SignUp/Manager/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("storeName").description("가게 이름"),
                                            fieldWithPath("storeSector").description("가게 업종"),
                                            fieldWithPath("storeAddress").description("가게 주소")                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("중복된 로그인 아이디가 존재한다면 가게 사장님 등록에 실패한다")
        void throwExceptionByDuplicateLoginId() throws Exception {
            // given
            doReturn(STORE_ID)
                    .when(storeService)
                    .save(anyString(), anyString(), anyString());
            doThrow(ApplicationException.type(UserErrorCode.DUPLICATE_LOGIN_ID))
                    .when(userService)
                    .saveManager(any(), anyLong());

            // when
            final SignUpManagerRequest request = createSignUpManagerRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .with(csrf())
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
                                    "UserApi/SignUp/Manager/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("storeName").description("가게 이름"),
                                            fieldWithPath("storeSector").description("가게 업종"),
                                            fieldWithPath("storeAddress").description("가게 주소")                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("가게 사장님 등록에 성공한다")
        @WithMockUser
        void success() throws Exception {
            // given
            doReturn(STORE_ID)
                    .when(storeService)
                    .save(anyString(), anyString(), anyString());
            doReturn(USER_ID)
                    .when(userService)
                    .saveManager(any(), anyLong());

            // when
            final SignUpManagerRequest request = createSignUpManagerRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final UserErrorCode expectedError = UserErrorCode.DUPLICATE_LOGIN_ID;
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "UserApi/SignUp/Manager/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("storeName").description("가게 이름"),
                                            fieldWithPath("storeSector").description("가게 업종"),
                                            fieldWithPath("storeAddress").description("가게 주소")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("아르바이트생 등록 API [POST /api/user/part-timer]")
    class signUpPartTimer {
        private static final String BASE_URL = "/api/user/part-timer";
        private static final Long USER_ID = 1L;

        @Test
        @DisplayName("중복된 로그인 아이디가 있다면 아르바이트생 등록에 실패한다")
        void throwExceptionByDuplicateLoginId() throws Exception {
            // given
            doThrow(ApplicationException.type(UserErrorCode.DUPLICATE_LOGIN_ID))
                    .when(userService)
                    .savePartTimer(any(), anyString(), anyString());

            // when
            final SignUpPartTimerRequest request = createSignUpPartTimerRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .with(csrf())
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
                                    "UserApi/SignUp/PartTimer/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("storeName").description("가게 이름"),
                                            fieldWithPath("storeCode").description("가게 코드")
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
        @DisplayName("가게 코드가 일치하지 않으면 아르바이트생 등록에 실패한다")
        void throwExceptionByInvalidStoreCode() throws Exception {
            // given
            doThrow(ApplicationException.type(UserErrorCode.INVALID_STORE_CODE))
                    .when(userService)
                    .savePartTimer(any(), anyString(), anyString());

            // when
            final SignUpPartTimerRequest request = createSignUpPartTimerRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final UserErrorCode expectedError = UserErrorCode.INVALID_STORE_CODE;
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
                                    "UserApi/SignUp/PartTimer/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("storeName").description("가게 이름"),
                                            fieldWithPath("storeCode").description("가게 코드")
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
        @DisplayName("아르바이트생 등록에 성공한다")
        void success() throws Exception {
            // given
            doReturn(USER_ID)
                    .when(userService)
                    .savePartTimer(any(), anyString(), anyString());

            // when
            final SignUpPartTimerRequest request = createSignUpPartTimerRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final UserErrorCode expectedError = UserErrorCode.INVALID_STORE_CODE;
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "UserApi/SignUp/PartTimer/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("storeName").description("가게 이름"),
                                            fieldWithPath("storeCode").description("가게 코드")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("슈퍼바이저 등록 API [POST /api/user/supervisor]")
    class signUpSupervisor {
        private static final String BASE_URL = "/api/user/supervisor";
        private static final Long USER_ID = 1L;

        @Test
        @DisplayName("중복된 로그인 아이디가 있다면 슈퍼바이저 등록에 실패한다")
        void throwExceptionByDuplicateLoginId() throws Exception {
            // given
            doThrow(ApplicationException.type(UserErrorCode.DUPLICATE_LOGIN_ID))
                    .when(userService)
                    .saveSupervisor(any(), anyString(), anyString());

            // when
            final SignUpSupervisorRequest request = createSignUpSupervisorRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .with(csrf())
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
                                    "UserApi/SignUp/Supervisor/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("companyName").description("회사 이름"),
                                            fieldWithPath("companyCode").description("회사 코드")
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
        @DisplayName("중복된 로그인 아이디가 있다면 슈퍼바이저 등록에 실패한다")
        void throwExceptionByInvalidCompanyCode() throws Exception {
            // given
            doThrow(ApplicationException.type(UserErrorCode.INVALID_COMPANY_CODE))
                    .when(userService)
                    .saveSupervisor(any(), anyString(), anyString());

            // when
            final SignUpSupervisorRequest request = createSignUpSupervisorRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final UserErrorCode expectedError = UserErrorCode.INVALID_COMPANY_CODE;
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
                                    "UserApi/SignUp/Supervisor/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("birth").description("생일"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("loginId").description("아이디"),
                                            fieldWithPath("password").description("비밀번호"),
                                            fieldWithPath("phoneNumber").description("전화번호"),
                                            fieldWithPath("companyName").description("회사 이름"),
                                            fieldWithPath("companyCode").description("회사 코드")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

    }

    private SignUpManagerRequest createSignUpManagerRequest() {
        return new SignUpManagerRequest(
                SAEWOO.getName(),
                SAEWOO.getBirth(),
                SAEWOO.getEmail(),
                SAEWOO.getLoginId(),
                SAEWOO.getPassword(),
                SAEWOO.getPhoneNumber(),
                "스타벅스 - 광화문점",
                "카페",
                "서울시 중구"
        );
    }

    private SignUpPartTimerRequest createSignUpPartTimerRequest() {
        return new SignUpPartTimerRequest(
                SAEWOO.getName(),
                SAEWOO.getBirth(),
                SAEWOO.getEmail(),
                SAEWOO.getPhoneNumber(),
                SAEWOO.getLoginId(),
                SAEWOO.getPassword(),
                "스타벅스 - 광화문점",
                "ABC123"
        );
    }

    private SignUpSupervisorRequest createSignUpSupervisorRequest() {
        return new SignUpSupervisorRequest(
                SAEWOO.getName(),
                SAEWOO.getBirth(),
                SAEWOO.getEmail(),
                SAEWOO.getPhoneNumber(),
                SAEWOO.getLoginId(),
                SAEWOO.getPassword(),
                "A 납품업체",
                "ABC123"
        );
    }

}