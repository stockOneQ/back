package umc.stockoneqback.comment.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.comment.controller.dto.CommentListResponse;
import umc.stockoneqback.comment.controller.dto.CustomCommentListResponse;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.reply.controller.dto.ReplyListResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.CommentFixture.*;
import static umc.stockoneqback.fixture.ReplyFixture.*;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.ELLA;

@DisplayName("Comment [Controller Layer] -> CommentListApiController 테스트")
class CommentListApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("댓글 목록 조회 API [GET /api/comments/{boardId}]")
    class getCommentList {
        private static final String BASE_URL = "/api/comments/{boardId}";
        private static final int PAGE = 0;
        private static final Long USER_ID = 1L;
        private static final Long BOARD_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 댓글 목록 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, BOARD_ID)
                    .param("page", String.valueOf(PAGE));

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
                                    "CommentApi/List/All/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("boardId").description("댓글을 조회할 게시글 ID")
                                    ),
                                    requestParameters(
                                            parameterWithName("page").description("page 번호(1페이지 = 0)")
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
        @DisplayName("댓글 목록 조회(등록순)에 성공한다")
        void success() throws Exception{
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getCustomCommentListResponse())
                    .when(commentListService)
                    .getCommentList(USER_ID, BOARD_ID, PAGE);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, BOARD_ID)
                    .param("page", String.valueOf(PAGE))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "CommentApi/List/All/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("boardId").description("댓글을 조회할 게시글 ID")
                                    ),
                                    requestParameters(
                                            parameterWithName("page").description("page 번호(1페이지 = 0)")
                                    ),
                                    responseFields(
                                            fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                            fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글 수"),
                                            fieldWithPath("pageInfo.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부(마지막 페이지인 경우만 false)"),
                                            fieldWithPath("pageInfo.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 게시글 수"),
                                            fieldWithPath("CommentListResponse[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                            fieldWithPath("CommentListResponse[].image").type(JsonFieldType.ARRAY).description("댓글 이미지").optional(),
                                            fieldWithPath("CommentListResponse[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                            fieldWithPath("CommentListResponse[].createdDate").type(JsonFieldType.STRING).description("댓글 최초 등록 시간"),
                                            fieldWithPath("CommentListResponse[].writerId").type(JsonFieldType.STRING).description("댓글 작성자 loginId"),
                                            fieldWithPath("CommentListResponse[].writerName").type(JsonFieldType.STRING).description("댓글 작성자 이름"),
                                            fieldWithPath("CommentListResponse[].replyList[].id").type(JsonFieldType.NUMBER).description("대댓글 ID"),
                                            fieldWithPath("CommentListResponse[].replyList[].image").type(JsonFieldType.ARRAY).description("대댓글 이미지").optional(),
                                            fieldWithPath("CommentListResponse[].replyList[].content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                            fieldWithPath("CommentListResponse[].replyList[].createdDate").type(JsonFieldType.STRING).description("대댓글 최초 등록 시간"),
                                            fieldWithPath("CommentListResponse[].replyList[].writerId").type(JsonFieldType.STRING).description("대댓글 작성자 loginId"),
                                            fieldWithPath("CommentListResponse[].replyList[].writerName").type(JsonFieldType.STRING).description("대댓글 작성자 이름")
                                    )
                            )
                    );
        }
    }

    private List<ReplyListResponse> createReplyListResponses() {
        List<ReplyListResponse> replyLists = new ArrayList<>();
        replyLists.add(new ReplyListResponse(1L, null, REPLY_0.getContent(), LocalDateTime.now(), ELLA.getLoginId(), ELLA.getName()));
        replyLists.add(new ReplyListResponse(2L, null, REPLY_1.getContent(), LocalDateTime.now(), ELLA.getLoginId(), ELLA.getName()));
        replyLists.add(new ReplyListResponse(3L, null, REPLY_2.getContent(), LocalDateTime.now(), ELLA.getLoginId(), ELLA.getName()));
        replyLists.add(new ReplyListResponse(4L, null, REPLY_3.getContent(), LocalDateTime.now(), ELLA.getLoginId(), ELLA.getName()));
        return replyLists;
    }

    private List<CommentListResponse> createCommentListResponses() {
        List<CommentListResponse> commentLists = new ArrayList<>();
        commentLists.add(new CommentListResponse(1L, null, COMMENT_0.getContent(), LocalDateTime.now(), ANNE.getLoginId(), ANNE.getName(), createReplyListResponses()));
        commentLists.add(new CommentListResponse(2L, null, COMMENT_1.getContent(), LocalDateTime.now(), ANNE.getLoginId(), ANNE.getName(), createReplyListResponses()));
        commentLists.add(new CommentListResponse(3L, null, COMMENT_2.getContent(), LocalDateTime.now(), ANNE.getLoginId(), ANNE.getName(), createReplyListResponses()));
        commentLists.add(new CommentListResponse(4L, null, COMMENT_3.getContent(), LocalDateTime.now(), ANNE.getLoginId(), ANNE.getName(), createReplyListResponses()));
        return commentLists;
    }

    private CustomCommentListResponse.CustomPageable createCustomPageable() {
        return new CustomCommentListResponse.CustomPageable(1, 4, false, 4);
    }

    private CustomCommentListResponse getCustomCommentListResponse() {
        return new CustomCommentListResponse(createCustomPageable(), createCommentListResponses());
    }
}

