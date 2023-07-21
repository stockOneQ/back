package umc.stockoneqback.board.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.board.controller.dto.BoardRequest;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;

@DisplayName("Board [Controller Layer] -> BoardApiController 테스트")
public class BoardApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("게시글 등록 API [POST /api/boards/{writerId}]")
    class createBoard {
        private static final String BASE_URL = "/api/boards/{writerId}";
        private static final Long WRITER_ID = 1L;

        @Test
        @DisplayName("게시글 등록에 성공한다")
        void success() throws Exception {
            // given
            doReturn(1L)
                    .when(boardService)
                    .create(anyLong(), any(), any(), any());

            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, WRITER_ID)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isCreated()
                    )
                    .andDo(
                            document(
                                    "UserApi/Create/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("게시글 작성자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("등록할 제목"),
                                            fieldWithPath("file").description("등록할 파일"),
                                            fieldWithPath("content").description("등록할 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("게시글 수정 API [PATCH /api/boards/{writerId}/{boardId}]")
    class updateBoard {
        private static final String BASE_URL = "/api/boards/{writerId}/{boardId}";
        private static final Long WRITER_ID = 1L;
        private static final Long BOARD_ID = 2L;

        @Test
        @DisplayName("다른 사람의 게시글은 수정할 수 없다")
        void throwExceptionByUserIsNotBoardWriter() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.USER_IS_NOT_BOARD_WRITER))
                    .when(boardService)
                    .update(anyLong(), anyLong(), any(), any(), any());

            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, WRITER_ID, BOARD_ID)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final BoardErrorCode expectedError = BoardErrorCode.USER_IS_NOT_BOARD_WRITER;
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
                                    "BoardApi/Update/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("게시글 작성자 ID(PK)"),
                                            parameterWithName("boardId").description("수정할 게시글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("수정할 제목"),
                                            fieldWithPath("file").description("수정할 파일"),
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
        @DisplayName("게시글 수정에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(boardService)
                    .update(anyLong(), anyLong(), any(), any(), any());

            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, WRITER_ID, BOARD_ID)
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
                                            parameterWithName("writerId").description("게시글 작성자 ID(PK)"),
                                            parameterWithName("boardId").description("수정할 게시글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("수정할 제목"),
                                            fieldWithPath("content").description("수정할 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("게시글 삭제 API [DELETE /api/boards/{writerId}/{boardId}]")
    class deleteBoard {
        private static final String BASE_URL = "/api/boards/{writerId}/{boardId}";
        private static final Long WRITER_ID = 1L;
        private static final Long BOARD_ID = 2L;

        @Test
        @DisplayName("다른 사람의 게시글은 삭제할 수 없다")
        void throwExceptionByUserIsNotBoardWriter() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.USER_IS_NOT_BOARD_WRITER))
                    .when(boardService)
                    .delete(anyLong(),anyLong());

            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, WRITER_ID, BOARD_ID)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final BoardErrorCode expectedError = BoardErrorCode.USER_IS_NOT_BOARD_WRITER;
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
                                    "BoardApi/Delete/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("게시글 작성자 ID(PK)"),
                                            parameterWithName("boardId").description("삭제할 게시글 ID(PK)")
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
        @DisplayName("게시글 삭제에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(boardService)
                    .delete(anyLong(), anyLong());

            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, WRITER_ID, BOARD_ID)
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
                                    "BoardApi/Delete/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("writerId").description("게시글 작성자 ID(PK)"),
                                            parameterWithName("boardId").description("삭제할 게시글 ID(PK)")
                                    )
                            )
                    );
        }
    }

    private BoardRequest createBoardRequest() {
        return new BoardRequest(BOARD_0.getTitle(), BOARD_0.getFile(), BOARD_0.getContent());
    }
}

