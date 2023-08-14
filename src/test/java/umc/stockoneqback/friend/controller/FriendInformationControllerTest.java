package umc.stockoneqback.friend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.friend.infra.query.dto.response.FriendInformation;
import umc.stockoneqback.friend.service.dto.FriendAssembler;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
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

@DisplayName("Friend [Controller Layer] -> FriendInformationController 테스트")
class FriendInformationControllerTest extends ControllerTest {
    @Nested
    @DisplayName("친구 리스트 조회 API [GET /api/friends]")
    class getFriends {
        private static final String BASE_URL = "/api/friends";
        private static final Long USER_ID = 1L;
        private static final Long LAST_USER_ID = -1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 친구 리스트 조회에 실패한다")
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
                                    "FriendApi/Information/Friends/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 친구 ID")
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
        @DisplayName("친구 리스트 조회에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getAcceptFriendAssembler())
                    .when(friendInformationService)
                    .getFriends(USER_ID, LAST_USER_ID);

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
                                    "FriendApi/Information/Friends/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 친구 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("friends[].id").description("친구 ID"),
                                            fieldWithPath("friends[].name").description("친구 이름"),
                                            fieldWithPath("friends[].storeName").description("친구 가게 이름"),
                                            fieldWithPath("friends[].phoneNumber").description("친구 연락처"),
                                            fieldWithPath("friends[].friendStatus").description("친구 상태 ['친구 요청', '친구 수락']"),
                                            fieldWithPath("friends[].lastModifiedDate").description("친구 상태 마지막 수정 시간")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("수락 대기중인 친구 리스트 조회 API [GET /api/friends/waiting]")
    class getWaitingFriends {
        private static final String BASE_URL = "/api/friends/waiting";
        private static final Long USER_ID = 1L;
        private static final Long LAST_USER_ID = -1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 수락 대기중인 친구 리스트 조회에 실패한다")
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
                                    "FriendApi/Information/WaitingFriends/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 친구 ID")
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
        @DisplayName("수락 대기중인 친구 리스트 조회에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getRequestFriendAssembler())
                    .when(friendInformationService)
                    .getWaitingFriends(USER_ID, LAST_USER_ID);

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
                                    "FriendApi/Information/WaitingFriends/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 친구 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("friends[].id").description("친구 ID"),
                                            fieldWithPath("friends[].name").description("친구 이름"),
                                            fieldWithPath("friends[].storeName").description("친구 가게 이름"),
                                            fieldWithPath("friends[].phoneNumber").description("친구 연락처"),
                                            fieldWithPath("friends[].friendStatus").description("친구 상태 ['친구 요청', '친구 수락']"),
                                            fieldWithPath("friends[].lastModifiedDate").description("친구 상태 마지막 수정 시간")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("요청이 들어온 친구 리스트 조회 API [GET /api/friends/requested]")
    class getRequestedFriends {
        private static final String BASE_URL = "/api/friends/requested";
        private static final Long USER_ID = 1L;
        private static final Long LAST_USER_ID = -1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 요청이 들어온 친구 리스트 조회에 실패한다")
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
                                    "FriendApi/Information/RequestedFriends/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 친구 ID")
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
        @DisplayName("요청이 들어온 친구 리스트 조회에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getRequestFriendAssembler())
                    .when(friendInformationService)
                    .getRequestedFriends(USER_ID, LAST_USER_ID);

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
                                    "FriendApi/Information/RequestedFriends/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막으로 조회된 친구 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("friends[].id").description("친구 ID"),
                                            fieldWithPath("friends[].name").description("친구 이름"),
                                            fieldWithPath("friends[].storeName").description("친구 가게 이름"),
                                            fieldWithPath("friends[].phoneNumber").description("친구 연락처"),
                                            fieldWithPath("friends[].friendStatus").description("친구 상태 ['친구 요청', '친구 수락']"),
                                            fieldWithPath("friends[].lastModifiedDate").description("친구 상태 마지막 수정 시간")
                                    )
                            )
                    );
        }
    }

    private FriendAssembler getAcceptFriendAssembler() {
        return new FriendAssembler(
                List.of(
                        new FriendInformation(1L, "홍길동", "스타벅스 광화문점", "01012345678", RelationStatus.ACCEPT, LocalDateTime.now()),
                        new FriendInformation(2L, "이순신", "투썸플레이스 강남역점", "01023456789", RelationStatus.ACCEPT, LocalDateTime.now()),
                        new FriendInformation(3L, "김철수", "커피빈 홍대입구역점", "01034567890", RelationStatus.ACCEPT, LocalDateTime.now())
                )
        );
    }

    private FriendAssembler getRequestFriendAssembler() {
        return new FriendAssembler(
                List.of(
                        new FriendInformation(1L, "홍길동", "스타벅스 광화문점", "01012345678", RelationStatus.REQUEST, LocalDateTime.now()),
                        new FriendInformation(2L, "이순신", "투썸플레이스 강남역점", "01023456789", RelationStatus.REQUEST, LocalDateTime.now()),
                        new FriendInformation(3L, "김철수", "커피빈 홍대입구역점", "01034567890", RelationStatus.REQUEST, LocalDateTime.now())
                )
        );
    }
}