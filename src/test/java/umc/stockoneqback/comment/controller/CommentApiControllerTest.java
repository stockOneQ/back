package umc.stockoneqback.comment.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.comment.controller.dto.CommentRequest;
import umc.stockoneqback.comment.exception.CommentErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;

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
import static umc.stockoneqback.fixture.CommentFixture.COMMENT_0;

@DisplayName("Comment [Controller Layer] -> CommentApiController 테스트")
public class CommentApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("댓글 등록 API [POST /api/comments/{writerId}/{boardId}]")
    class createBoard {
        private static final String BASE_URL = "/api/comments/{writerId}/{boardId}";
        private static final Long WRITER_ID = 1L;
        private static final Long BOARD_ID = 2L;

        @Test
        @DisplayName("댓글 등록에 성공한다")
        void success() throws Exception {
            // given
            doReturn(1L)
                    .when(commentService)
                    .create(anyLong(), any(), any(), any());

            // when
            final CommentRequest request = createCommentRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, WRITER_ID, BOARD_ID)
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
                                    "CommentApi/Create/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("boardId").description("댓글 작성자 ID(PK)"),
                                            parameterWithName("writerId").description("등록할 게시글 ID(PK)")
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
    @DisplayName("댓글 수정 API [PATCH /api/comments/{writerId}/{commentId}]")
    class updateBoard {
        private static final String BASE_URL = "/api/comments/{writerId}/{commentId}";
        private static final Long WRITER_ID = 1L;
        private static final Long COMMENT_ID = 2L;

        @Test
        @DisplayName("다른 사람의 댓글은 수정할 수 없다")
        void throwExceptionByUserIsNotCommentWriter() throws Exception {
            // given
            doThrow(BaseException.type(CommentErrorCode.USER_IS_NOT_COMMENT_WRITER))
                    .when(commentService)
                    .update(anyLong(), anyLong(), any(), any());

            // when
            final CommentRequest request = createCommentRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, WRITER_ID, COMMENT_ID)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final CommentErrorCode expectedError = CommentErrorCode.USER_IS_NOT_COMMENT_WRITER;
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
                                    "CommentApi/Update/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("댓글 작성자 ID(PK)"),
                                            parameterWithName("commentId").description("수정할 댓글 ID(PK)")
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
        @DisplayName("댓글 수정에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(commentService)
                    .update(anyLong(), anyLong(), any(), any());

            // when
            final CommentRequest request = createCommentRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, WRITER_ID, COMMENT_ID)
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
                                    "BoardApi/Update/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("댓글 작성자 ID(PK)"),
                                            parameterWithName("commentId").description("수정할 댓글 ID(PK)")
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
    @DisplayName("댓글 삭제 API [DELETE /api/comments/{writerId}/{commentId}]")
    class deleteBoard {
        private static final String BASE_URL = "/api/comments/{writerId}/{commentId}";
        private static final Long WRITER_ID = 1L;
        private static final Long COMMENT_ID = 2L;

        @Test
        @DisplayName("다른 사람의 댓글은 삭제할 수 없다")
        void throwExceptionByUserIsNotCommentWriter() throws Exception {
            // given
            doThrow(BaseException.type(CommentErrorCode.USER_IS_NOT_COMMENT_WRITER))
                    .when(commentService)
                    .delete(anyLong(),anyLong());

            // when
            final CommentRequest request = createCommentRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, WRITER_ID, COMMENT_ID)
                    .with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final CommentErrorCode expectedError = CommentErrorCode.USER_IS_NOT_COMMENT_WRITER;
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
                                    "CommentApi/Delete/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("댓글 작성자 ID(PK)"),
                                            parameterWithName("commentId").description("삭제할 댓글 ID(PK)")
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
        @DisplayName("댓글 삭제에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(commentService)
                    .delete(anyLong(), anyLong());

            // when
            final CommentRequest request = createCommentRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, WRITER_ID, COMMENT_ID)
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
                                    "CommentApi/Delete/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("댓글 작성자 ID(PK)"),
                                            parameterWithName("commentId").description("삭제할 댓글 ID(PK)")
                                    )
                            )
                    );
        }
    }

    private CommentRequest createCommentRequest() {
        return new CommentRequest(COMMENT_0.getImage(), COMMENT_0.getContent());
    }
}
