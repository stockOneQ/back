package umc.stockoneqback.board.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.board.controller.dto.BoardListResponse;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.board.infra.query.dto.BoardList;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
import static umc.stockoneqback.fixture.BoardFixture.*;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;

@DisplayName("Board [Controller Layer] -> BoardListApiController 테스트")
class BoardListApiControllerTest extends ControllerTest {
    private static final String INVALID_SORT = "댓글순";
    private static final String INVALID_SEARCH = "댓글";
    private static final String SORT_BY_TIME = "최신순";
    private static final String SEARCH_TYPE = "제목";
    private static final String SEARCH_WORD = "제목";
    @Nested
    @DisplayName("게시글 목록 조회 API [GET /api/boards]")
    class getBoardList {
        private static final String BASE_URL = "/api/boards";
        private static final Long LAST_USER_ID = -1L;
        private static final Long USER_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 게시글 목록 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("sort", SORT_BY_TIME)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD);

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
                                    "BoardApi/List/All/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준(최신순/조회순)"),
                                            parameterWithName("search").description("검색 조건(제목/내용/작성자)"),
                                            parameterWithName("word").description("검색어")
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
        @DisplayName("유효하지 않은 정렬 방식이라면 게시글 목록 조회에 실패한다")
        void throwExceptionByNotFoundSortCondition() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.NOT_FOUND_SORT_CONDITION))
                    .when(boardListService)
                    .getBoardList(anyLong(), eq(LAST_USER_ID), eq(INVALID_SORT), eq(SEARCH_TYPE), eq(SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("sort", INVALID_SORT)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final BoardErrorCode expectedError = BoardErrorCode.NOT_FOUND_SORT_CONDITION;
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
                                    "BoardApi/List/All/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준(최신순/조회순)"),
                                            parameterWithName("search").description("검색 조건(제목/내용/작성자)"),
                                            parameterWithName("word").description("검색어")
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
        @DisplayName("유효하지 않은 검색 조건이라면 게시글 목록 검색에 실패한다")
        void throwExceptionByNotFoundSearchType() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.NOT_FOUND_SEARCH_TYPE))
                    .when(boardListService)
                    .getBoardList(anyLong(), eq(LAST_USER_ID), eq(SORT_BY_TIME), eq(INVALID_SEARCH), eq(SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("sort", SORT_BY_TIME)
                    .param("search", INVALID_SEARCH)
                    .param("word", SEARCH_WORD)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final BoardErrorCode expectedError = BoardErrorCode.NOT_FOUND_SEARCH_TYPE;
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
                                    "BoardApi/List/All/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준(최신순/조회순)"),
                                            parameterWithName("search").description("검색 조건(제목/내용/작성자)"),
                                            parameterWithName("word").description("검색어")
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
        @DisplayName("정렬 기준과 검색에 따른 게시글 목록 조회에 성공한다")
        void success() throws Exception{
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getBoardListResponse())
                    .when(boardListService)
                    .getBoardList(USER_ID, LAST_USER_ID, SORT_BY_TIME, SEARCH_TYPE, SEARCH_WORD);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("sort", SORT_BY_TIME)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "BoardApi/List/All/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준(최신순/조회순)"),
                                            parameterWithName("search").description("검색 조건(제목/내용/작성자)"),
                                            parameterWithName("word").description("검색어")
                                    ),
                                    responseFields(
                                            fieldWithPath("total").type(JsonFieldType.NUMBER).description("전체 게시글 수"),
                                            fieldWithPath("boardListResponse[].id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                            fieldWithPath("boardListResponse[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                            fieldWithPath("boardListResponse[].content").type(JsonFieldType.STRING).description("내용 미리보기"),
                                            fieldWithPath("boardListResponse[].hit").type(JsonFieldType.NUMBER).description("조회 수"),
                                            fieldWithPath("boardListResponse[].createdDate").type(JsonFieldType.STRING).description("게시글 최초 등록 시간"),
                                            fieldWithPath("boardListResponse[].comment").type(JsonFieldType.NUMBER).description("댓글 수"),
                                            fieldWithPath("boardListResponse[].like").type(JsonFieldType.NUMBER).description("좋아요 수")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("내가 쓴 글 목록 조회 API [GET /api/boards/my]")
    class myBoardList {
        private static final String BASE_URL = "/api/boards/my";
        private static final Long LAST_USER_ID = -1L;
        private static final Long USER_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 내가 쓴 글 목록 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("sort", SORT_BY_TIME)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD);

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
                                    "BoardApi/List/My/View/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준(최신순/조회순)"),
                                            parameterWithName("search").description("검색 조건(제목/내용)"),
                                            parameterWithName("word").description("검색어")
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
        @DisplayName("유효하지 않은 정렬 방식이라면 내가 쓴 글 목록 조회에 실패한다")
        void throwExceptionByNotFoundSortCondition() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.NOT_FOUND_SORT_CONDITION))
                    .when(boardListService)
                    .getMyBoardList(anyLong(), eq(LAST_USER_ID), eq(INVALID_SORT), eq(SEARCH_TYPE), eq(SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("sort", INVALID_SORT)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final BoardErrorCode expectedError = BoardErrorCode.NOT_FOUND_SORT_CONDITION;
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
                                    "BoardApi/List/My/View/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준(최신순/조회순)"),
                                            parameterWithName("search").description("검색 조건(제목/내용)"),
                                            parameterWithName("word").description("검색어")
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
        @DisplayName("유효하지 않은 검색 조건이라면 내가 쓴 글 검색에 실패한다")
        void throwExceptionByNotFoundSearchType() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.NOT_FOUND_SEARCH_TYPE))
                    .when(boardListService)
                    .getMyBoardList(anyLong(), eq(LAST_USER_ID), eq(SORT_BY_TIME), eq(INVALID_SEARCH), eq(SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("sort", SORT_BY_TIME)
                    .param("search", INVALID_SEARCH)
                    .param("word", SEARCH_WORD)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final BoardErrorCode expectedError = BoardErrorCode.NOT_FOUND_SEARCH_TYPE;
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
                                    "BoardApi/List/My/View/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준(최신순/조회순)"),
                                            parameterWithName("search").description("검색 조건(제목/내용)"),
                                            parameterWithName("word").description("검색어")
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
        @DisplayName("정렬 기준과 검색에 따른 내가 쓴 글 조회에 성공한다")
        void success() throws Exception{
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getBoardListResponse())
                    .when(boardListService)
                    .getMyBoardList(USER_ID, LAST_USER_ID, SORT_BY_TIME, SEARCH_TYPE, SEARCH_WORD);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", String.valueOf(LAST_USER_ID))
                    .param("sort", SORT_BY_TIME)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "BoardApi/List/My/View/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준(최신순/조회순)"),
                                            parameterWithName("search").description("검색 조건(제목/내용)"),
                                            parameterWithName("word").description("검색어")
                                    ),
                                    responseFields(
                                            fieldWithPath("total").type(JsonFieldType.NUMBER).description("전체 게시글 수"),
                                            fieldWithPath("boardListResponse[].id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                            fieldWithPath("boardListResponse[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                            fieldWithPath("boardListResponse[].content").type(JsonFieldType.STRING).description("내용 미리보기"),
                                            fieldWithPath("boardListResponse[].hit").type(JsonFieldType.NUMBER).description("조회 수"),
                                            fieldWithPath("boardListResponse[].createdDate").type(JsonFieldType.STRING).description("게시글 최초 등록 시간"),
                                            fieldWithPath("boardListResponse[].comment").type(JsonFieldType.NUMBER).description("댓글 수"),
                                            fieldWithPath("boardListResponse[].like").type(JsonFieldType.NUMBER).description("좋아요 수")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("내가 쓴 글 게시글 삭제 API [DELETE /api/boards/my]")
    class deleteMyBoard {
        private static final String BASE_URL = "/api/boards/my";
        private static final Long BOARD_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 내가 쓴 글 게시글 삭제에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL)
                    .param("boardId", String.valueOf(BOARD_ID));

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
                                    "BoardApi/List/My/Delete/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("boardId").description("선택된 게시글들 ID")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("다른 사람의 게시글은 삭제할 수 없다")
        void throwExceptionByUserIsNotBoardWriter() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.USER_IS_NOT_BOARD_WRITER))
                    .when(boardListService)
                    .deleteMyBoard(anyLong(), anyList());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL)
                    .param("boardId", String.valueOf(BOARD_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

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
                                    "BoardApi/List/My/Delete/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("boardId").description("선택된 게시글들 ID")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("내가 쓴 글 게시글 삭제에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(boardListService)
                    .deleteMyBoard(anyLong(), anyList());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL)
                    .param("boardId", String.valueOf(BOARD_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "BoardApi/List/My/Delete/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("boardId").description("선택된 게시글들 ID")
                                    )
                            )
                    );
        }
    }

    private BoardListResponse getBoardListResponse() {
        return new BoardListResponse(
                4,
                List.of(
                        new BoardList(1L, BOARD_0.getTitle(), BOARD_0.getContent(), 1, LocalDateTime.now(), 0, 0),
                        new BoardList(2L, BOARD_1.getTitle(), BOARD_1.getContent(), 2, LocalDateTime.now(), 0, 0),
                        new BoardList(3L, BOARD_2.getTitle(), BOARD_2.getContent(), 3, LocalDateTime.now(), 0, 0),
                        new BoardList(4L, BOARD_3.getTitle(), BOARD_3.getContent(), 4, LocalDateTime.now(), 0, 0)
                )
        );
    }
}
