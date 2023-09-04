package umc.stockoneqback.board.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.controller.dto.CustomBoardListResponse;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.board.infra.query.dto.BoardList;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.*;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Board [Service Layer] -> BoardListService 테스트")
class BoardListServiceTest extends ServiceTest {
    @Autowired
    private BoardListService boardListService;
    @Autowired
    private BoardFindService boardFindService;

    private User writer;
    private User not_writer;
    private Long userId;

    private final Board[] boardList = new Board[10];
    private final List<Long> selectedBoardId = new ArrayList<>();

    private static final int PAGE = 0;
    private static final String SORT_BY = "조회순";
    private static final String INVALID_SORT = "댓글순";
    private static final String INVALID_SEARCH = "댓글";
    private static final String SEARCH_TYPE = "제목";
    private static final String SEARCH_WORD = "제목";
    private static final int PAGE_SIZE = 7;

    @BeforeEach
    void setUp() {
        boardRepository.deleteAll();
        writer = userRepository.save(ANNE.toUser());
        not_writer = userRepository.save(ELLA.toUser());
        userId = writer.getId();

        boardList[0] = boardRepository.save(BOARD_0.toBoard(writer));
        boardList[1] = boardRepository.save(BOARD_1.toBoard(writer));
        boardList[2] = boardRepository.save(BOARD_2.toBoard(writer));
        boardList[3] = boardRepository.save(BOARD_3.toBoard(writer));
        boardList[4] = boardRepository.save(BOARD_4.toBoard(writer));
        boardList[5] = boardRepository.save(BOARD_5.toBoard(writer));
        boardList[6] = boardRepository.save(BOARD_6.toBoard(writer));
        boardList[7] = boardRepository.save(BOARD_7.toBoard(writer));
        boardList[8] = boardRepository.save(BOARD_8.toBoard(writer));
        boardList[9] = boardRepository.save(BOARD_9.toBoard(writer));

        for (Board board : boardList) {
            selectedBoardId.add(board.getId());
        }
    }

    @AfterEach
    void clearAll() {
        boardRepository.deleteAll();
    }

    @Nested
    @DisplayName("전체 게시글 조회")
    class allBoardList {
        @Test
        @DisplayName("유효한 정렬 조건이 아니면 게시글 목록 조회에 실패한다")
        void throwNotFoundSortCondition() {
            // when - then
            assertThatThrownBy(() -> boardListService.getBoardList(userId, PAGE, INVALID_SORT, SEARCH_TYPE, SEARCH_WORD))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.NOT_FOUND_SORT_CONDITION.getMessage());
        }

        @Test
        @DisplayName("유효한 검색 조건이 아니면 게시글 목록 검색에 실패한다")
        void throwNotFoundSearchType() {
            // when - then
            assertThatThrownBy(() -> boardListService.getBoardList(userId, PAGE, SORT_BY, INVALID_SEARCH, SEARCH_WORD))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.NOT_FOUND_SEARCH_TYPE.getMessage());
        }

        @Test
        @DisplayName("정렬 기준과 검색에 따른 게시글 목록 조회에 성공한다")
        void getBoardListByHit() {
            // when
            CustomBoardListResponse<BoardList> boardListResponse = boardListService.getBoardList(userId, PAGE, SORT_BY, SEARCH_TYPE, SEARCH_WORD);

            // then
            assertThat(boardListResponse.getBoardList().size()).isLessThanOrEqualTo(PAGE_SIZE);
            assertThat(boardListResponse.getBoardList().size()).isEqualTo(PAGE_SIZE);

            assertAll(
                    () -> assertThat(boardListResponse.getBoardList().get(0).getId()).isEqualTo(boardList[9].getId()),
                    () -> assertThat(boardListResponse.getBoardList().get(0).getTitle()).isEqualTo(boardList[9].getTitle()),
                    () -> assertThat(boardListResponse.getBoardList().get(0).getContent()).isEqualTo(boardList[9].getContent()),
                    () -> assertThat(boardListResponse.getBoardList().get(0).getHit()).isEqualTo(boardList[9].getHit())
            );
        }
    }

    @Nested
    @DisplayName("내가 쓴 글 조회")
    class myBoardList {
        @Test
        @DisplayName("매니저가 아니거나 소멸 상태이면 게시글 목록 조회에 실패한다")
        void throwUserNotAllowed() {
            // given
            Long invalidUserId = userRepository.save(SAEWOO.toUser()).getId();

            // when - then
            assertThatThrownBy(() -> boardListService.getMyBoardList(invalidUserId, PAGE, SORT_BY, SEARCH_TYPE, SEARCH_WORD))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_NOT_ALLOWED.getMessage());
        }

        @Test
        @DisplayName("유효한 정렬 조건이 아니면 내가 쓴 글 조회에 실패한다")
        void throwNotFoundSortCondition() {
            // when - then
            assertThatThrownBy(() -> boardListService.getMyBoardList(userId, PAGE, INVALID_SORT, SEARCH_TYPE, SEARCH_WORD))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.NOT_FOUND_SORT_CONDITION.getMessage());
        }

        @Test
        @DisplayName("유효한 검색 조건이 아니면 게시글 목록 검색에 실패한다")
        void throwNotFoundSearchType() {
            // when - then
            assertThatThrownBy(() -> boardListService.getMyBoardList(userId, PAGE, SORT_BY, INVALID_SEARCH, SEARCH_WORD))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.NOT_FOUND_SEARCH_TYPE.getMessage());
        }

        @Test
        @DisplayName("정렬 기준과 검색에 따른 게시글 목록 조회에 성공한다")
        void getMyBoardList() {
            // when
            CustomBoardListResponse<BoardList> myBoardList = boardListService.getMyBoardList(userId, PAGE, SORT_BY, SEARCH_TYPE, SEARCH_WORD);

            // then
            assertThat(myBoardList.getBoardList().size()).isLessThanOrEqualTo(PAGE_SIZE);
            assertThat(myBoardList.getBoardList().size()).isEqualTo(PAGE_SIZE);

            assertAll(
                    () -> assertThat(myBoardList.getBoardList().get(0).getId()).isEqualTo(boardList[9].getId()),
                    () -> assertThat(myBoardList.getBoardList().get(0).getTitle()).isEqualTo(boardList[9].getTitle()),
                    () -> assertThat(myBoardList.getBoardList().get(0).getContent()).isEqualTo(boardList[9].getContent()),
                    () -> assertThat(myBoardList.getBoardList().get(0).getHit()).isEqualTo(boardList[9].getHit())
            );
        }
    }

    @Nested
    @DisplayName("내가 쓴 글 게시글 삭제")
    class deleteMyBoard {
        @Test
        @DisplayName("다른 사람의 게시글은 삭제할 수 없다")
        void throwExceptionByUserNotBoardWriter() {
            // when - then
            assertThatThrownBy(() -> boardListService.deleteMyBoard(not_writer.getId(), selectedBoardId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.USER_IS_NOT_BOARD_WRITER.getMessage());
        }

        @Test
        @DisplayName("내가 쓴 글 삭제에 성공한다")
        void success() {
            // given
            boardListService.deleteMyBoard(writer.getId(), selectedBoardId);

            // when - then
            assertThatThrownBy(() -> boardFindService.findById(boardList[0].getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.BOARD_NOT_FOUND.getMessage());
        }
    }
}