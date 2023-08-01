package umc.stockoneqback.board.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.fixture.BoardListFixture;
import umc.stockoneqback.global.base.BaseException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.BoardListFixture.*;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;

@DisplayName("Board [Controller Layer] -> BoardListApiController 테스트")
class BoardListApiControllerTest extends ControllerTest {

    @Nested
    @DisplayName("정렬 기준에 따른 게시글 목록 조회 API [GET /api/boards]")
    class getBoardList {
        private static final String BASE_URL = "/api/boards";
        private static final String INVALID_SORT = "댓글순";
        private static final String SORT_BY_TIME = "최신순";
        private static final List<BoardListFixture> boardFixtureList = new ArrayList<>(
                Stream.of(BOARD_1, BOARD_2, BOARD_3, BOARD_4, BOARD_5,
                        BOARD_6, BOARD_7, BOARD_8, BOARD_9).collect(Collectors.toList())
        );

        @Test
        @DisplayName("유효하지 않은 정렬 방식이라면 게시글 목록 조회에 실패한다")
        void throwExceptionByInvalidSort() throws Exception {
            // given
            doThrow(BaseException.type(BoardErrorCode.NOT_FOUND_SORT_CONDITION))
                    .when(boardListService)
                    .getBoardList(anyLong(), isNull(), eq(INVALID_SORT));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", (String) null)
                    .param("sort", "방구멍아")
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
                                    "BoardListApi/getBoardList/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준")
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
        @DisplayName("최신순 기준 게시글 목록 조회에 성공한다")
        void success() throws Exception{
            // given
            doReturn(getBoardListResponse())
                    .when(boardListService)
                    .getBoardList(anyLong(), isNull(), eq(SORT_BY_TIME));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("last", (String) null)
                    .param("sort", SORT_BY_TIME)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result[0]").exists(),
                            jsonPath("$.result[0].title").value(boardFixtureList.get(8).getTitle()),
                            jsonPath("$.result[1]").exists(),
                            jsonPath("$.result[1].title").value(boardFixtureList.get(7).getTitle()),
                            jsonPath("$.result[2]").exists(),
                            jsonPath("$.result[2].title").value(boardFixtureList.get(6).getTitle()),
                            jsonPath("$.result[3]").exists(),
                            jsonPath("$.result[3].title").value(boardFixtureList.get(5).getTitle()),
                            jsonPath("$.result[4]").exists(),
                            jsonPath("$.result[4].title").value(boardFixtureList.get(4).getTitle()),
                            jsonPath("$.result[5]").exists(),
                            jsonPath("$.result[5].title").value(boardFixtureList.get(3).getTitle()),
                            jsonPath("$.result[6]").exists(),
                            jsonPath("$.result[6].title").value(boardFixtureList.get(2).getTitle())
                    )
                    .andDo(
                            document(
                                    "BoardListApi/getBoardList/Success1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("last").description("마지막 게시글 ID"),
                                            parameterWithName("sort").description("정렬 기준")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지"),
                                            fieldWithPath("result[].id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                            fieldWithPath("result[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                            fieldWithPath("result[].content").type(JsonFieldType.STRING).description("내용 미리보기"),
                                            fieldWithPath("result[].hit").type(JsonFieldType.NUMBER).description("조회 수"),
                                            fieldWithPath("result[].comment").type(JsonFieldType.NUMBER).description("댓글 수"),
                                            fieldWithPath("result[].like").type(JsonFieldType.NUMBER).description("좋아요 수")
                                    )
                            )
                    );
        }
    }
    private List<BoardListResponseD> getBoardListResponse() {
        List<BoardListResponseD> boardListResponse = new ArrayList<>();
        boardListResponse.add(
                new BoardListResponseD(9L, BOARD_9.getTitle(), BOARD_9.getContent(), BOARD_9.getHit(), 0,0));
        boardListResponse.add(
                new BoardListResponseD(8L, BOARD_8.getTitle(), BOARD_8.getContent(), BOARD_8.getHit(), 0,0));
        boardListResponse.add(
                new BoardListResponseD(7L, BOARD_7.getTitle(), BOARD_7.getContent(), BOARD_7.getHit(), 0,0));
        boardListResponse.add(
                new BoardListResponseD(6L, BOARD_6.getTitle(), BOARD_6.getContent(), BOARD_6.getHit(), 0,0));
        boardListResponse.add(
                new BoardListResponseD(5L, BOARD_5.getTitle(), BOARD_5.getContent(), BOARD_5.getHit(), 0,0));
        boardListResponse.add(
                new BoardListResponseD(4L, BOARD_4.getTitle(), BOARD_4.getContent(), BOARD_4.getHit(), 0,0));
        boardListResponse.add(
                new BoardListResponseD(3L, BOARD_3.getTitle(), BOARD_3.getContent(), BOARD_3.getHit(), 0,0));
        return boardListResponse;
    }
}