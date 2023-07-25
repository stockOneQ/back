package umc.stockoneqback.reply.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.reply.controller.dto.ReplyRequest;
import umc.stockoneqback.reply.exception.ReplyErrorCode;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.ReplyFixture.REPLY_0;

@DisplayName("Reply [Controller Layer] -> ReplyApiController 테스트")
public class ReplyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("대댓글 등록 API [POST /api/replies/{writerId}/{commentId}]")
    class createBoard {
        private static final String BASE_URL = "/api/replies/{writerId}/{commentId}";
        private static final Long WRITER_ID = 1L;
        private static final Long COMMENT_ID = 2L;

        @Test
        @DisplayName("대댓글 등록에 성공한다")
        void success() throws Exception {
            // given
            doReturn(1L)
                    .when(replyService)
                    .create(anyLong(), any(), any(), any());

            // when
            final ReplyRequest request = createReplyRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, WRITER_ID, COMMENT_ID)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Create/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("대댓글 작성자 ID(PK)"),
                                            parameterWithName("commentId").description("등록할 댓글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("image").description("등록할 이미지"),
                                            fieldWithPath("content").description("등록할 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("대댓글 수정 API [PATCH /api/replies/{writerId}/{replyId}]")
    class updateBoard {
        private static final String BASE_URL = "/api/replies/{writerId}/{replyId}";
        private static final Long WRITER_ID = 1L;
        private static final Long REPLY_ID = 2L;

        @Test
        @DisplayName("다른 사람의 대댓글은 수정할 수 없다")
        void throwExceptionByUserIsNotReplyWriter() throws Exception {
            // given
            doThrow(BaseException.type(ReplyErrorCode.USER_IS_NOT_REPLY_WRITER))
                    .when(replyService)
                    .update(anyLong(), anyLong(), any(), any());

            // when
            final ReplyRequest request = createReplyRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, WRITER_ID, REPLY_ID)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final ReplyErrorCode expectedError = ReplyErrorCode.USER_IS_NOT_REPLY_WRITER;
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
                                    "ReplyApi/Update/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("댓글 작성자 ID(PK)"),
                                            parameterWithName("replyId").description("수정할 대댓글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("image").description("수정할 이미지"),
                                            fieldWithPath("content").description("수정할 내용")
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
        @DisplayName("대댓글 수정에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(replyService)
                    .update(anyLong(), anyLong(), any(), any());

            // when
            final ReplyRequest request = createReplyRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, WRITER_ID, REPLY_ID)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Update/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("대댓글 작성자 ID(PK)"),
                                            parameterWithName("replyId").description("수정할 대댓글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("image").description("수정할 이미지"),
                                            fieldWithPath("content").description("수정할 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("대댓글 삭제 API [DELETE /api/replies/{writerId}/{replyId}]")
    class deleteBoard {
        private static final String BASE_URL = "/api/replies/{writerId}/{replyId}";
        private static final Long WRITER_ID = 1L;
        private static final Long REPLY_ID = 2L;

        @Test
        @DisplayName("다른 사람의 대댓글은 삭제할 수 없다")
        void throwExceptionByUserIsNotReplyWriter() throws Exception {
            // given
            doThrow(BaseException.type(ReplyErrorCode.USER_IS_NOT_REPLY_WRITER))
                    .when(replyService)
                    .delete(anyLong(),anyLong());

            // when
            final ReplyRequest request = createReplyRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, WRITER_ID, REPLY_ID)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final ReplyErrorCode expectedError = ReplyErrorCode.USER_IS_NOT_REPLY_WRITER;
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
                                    "ReplyApi/Delete/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("대댓글 작성자 ID(PK)"),
                                            parameterWithName("replyId").description("삭제할 대댓글 ID(PK)")
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
        @DisplayName("대댓글 삭제에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(replyService)
                    .delete(anyLong(), anyLong());

            // when
            final ReplyRequest request = createReplyRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, WRITER_ID, REPLY_ID)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Delete/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("대댓글 작성자 ID(PK)"),
                                            parameterWithName("replyId").description("삭제할 대댓글 ID(PK)")
                                    )
                            )
                    );
        }
    }

    private ReplyRequest createReplyRequest() {
        return new ReplyRequest(REPLY_0.getImage(), REPLY_0.getContent());
    }
}
