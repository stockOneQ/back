package umc.stockoneqback.board.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.board.controller.dto.BoardRequest;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;

@DisplayName("Board [Controller Layer] -> BoardApiController 테스트")
public class BoardApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("게시글 등록 API [POST /api/boards]")
    class createBoard {
        private static final String BASE_URL = "/api/boards";

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 게시글 등록에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
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
                                    "BoardApi/Create/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("title").description("등록할 제목"),
                                            fieldWithPath("file").description("등록할 파일"),
                                            fieldWithPath("content").description("등록할 내용")
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
        @DisplayName("게시글 등록에 성공한다")
        void success() throws Exception {
            // given
            doReturn(1L)
                    .when(boardService)
                    .create(anyLong(), any(), any(), any());

            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "UserApi/Create/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
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
    @DisplayName("게시글 수정 API [PATCH /api/boards/{boardId}]")
    class updateBoard {
        private static final String BASE_URL = "/api/boards/{boardId}";
        private static final Long WRITER_ID = 1L;
        private static final Long BOARD_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 게시글 수정에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, BOARD_ID)
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
                                    "BoardApi/Update/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
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
        @DisplayName("다른 사람의 게시글은 수정할 수 없다")
        void throwExceptionByUserIsNotBoardWriter() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.USER_IS_NOT_BOARD_WRITER))
                    .when(boardService)
                    .update(anyLong(), anyLong(), any(), any(), any());

            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, BOARD_ID)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
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
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
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
                    .patch(BASE_URL, BOARD_ID)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "BoardApi/Update/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("boardId").description("수정할 게시글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").description("수정할 제목"),
                                            fieldWithPath("file").description("수정할 파일"),
                                            fieldWithPath("content").description("수정할 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("게시글 삭제 API [DELETE /api/boards/{boardId}]")
    class deleteBoard {
        private static final String BASE_URL = "/api/boards/{boardId}";
        private static final Long WRITER_ID = 1L;
        private static final Long BOARD_ID = 2L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 게시글 삭제에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, BOARD_ID)
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
                                    "BoardApi/Delete/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
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
        @DisplayName("다른 사람의 게시글은 삭제할 수 없다")
        void throwExceptionByUserIsNotBoardWriter() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.USER_IS_NOT_BOARD_WRITER))
                    .when(boardService)
                    .delete(anyLong(),anyLong());

            // when
            final BoardRequest request = createBoardRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, BOARD_ID)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
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
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
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
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(status().isNoContent())
                    .andDo(
                            document(
                                    "BoardApi/Delete/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
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

