package umc.stockoneqback.board.infra.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.controller.dto.CustomBoardListResponse;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.board.domain.BoardSearchType;
import umc.stockoneqback.board.infra.query.dto.BoardList;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.*;
import static umc.stockoneqback.fixture.UserFixture.ANNE;

@DisplayName("Board [Repository Layer] -> BoardListQueryRepository 테스트")
class BoardListQueryRepositoryImplTest extends RepositoryTest {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    private final Board[] boardList = new Board[10];

    private User writer;

    private static final BoardSearchType SEARCH_TYPE = BoardSearchType.TITLE;
    private static final String SEARCH_TITLE = "제목";
    private static final String SEARCH_CONTENT = "5";
    private static final int PAGE = 0;

    @BeforeEach
    void setUp() {
        boardRepository.deleteAll();
        writer = userRepository.save(ANNE.toUser());

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
    }

    @Test
    @DisplayName("검색 결과에 따른 게시글을 최신순으로 조회한다")
    void getBoardListOrderByTime() {
        // when
        CustomBoardListResponse<BoardList> boardListOrderByTime = boardRepository.getBoardListOrderByTime(SEARCH_TYPE, SEARCH_CONTENT, PAGE);

        // then
        assertAll(
                () -> assertThat(boardListOrderByTime.getBoardList().get(0).getId()).isEqualTo(boardList[5].getId()),
                () -> assertThat(boardListOrderByTime.getBoardList().get(0).getTitle()).isEqualTo(boardList[5].getTitle()),
                () -> assertThat(boardListOrderByTime.getBoardList().get(0).getContent()).isEqualTo(boardList[5].getContent()),
                () -> assertThat(boardListOrderByTime.getBoardList().get(0).getHit()).isEqualTo(boardList[5].getHit())
        );
    }

    @Test
    @DisplayName("검색 결과에 따른 게시글을 조회순으로 조회한다")
    void getBoardListOrderByHit() {
        // when
        CustomBoardListResponse<BoardList> boardListOrderByHit = boardRepository.getBoardListOrderByHit(SEARCH_TYPE, SEARCH_TITLE, PAGE);

        // then
        assertAll(
                () -> assertThat(boardListOrderByHit.getBoardList().get(0).getId()).isEqualTo(boardList[9].getId()),
                () -> assertThat(boardListOrderByHit.getBoardList().get(0).getTitle()).isEqualTo(boardList[9].getTitle()),
                () -> assertThat(boardListOrderByHit.getBoardList().get(0).getContent()).isEqualTo(boardList[9].getContent()),
                () -> assertThat(boardListOrderByHit.getBoardList().get(0).getHit()).isEqualTo(boardList[9].getHit())
        );
    }

    @Test
    @DisplayName("내가 쓴 글에서 검색 결과에 따른 게시글을 최신순으로 조회한다")
    void getMyBoardListOrderByTime() {
        // when
        CustomBoardListResponse<BoardList> myBoardList = boardRepository.getMyBoardListOrderByTime(writer.getId(), SEARCH_TYPE, SEARCH_CONTENT, PAGE);

        // then
        assertAll(
                () -> assertThat(myBoardList.getBoardList().get(0).getId()).isEqualTo(boardList[5].getId()),
                () -> assertThat(myBoardList.getBoardList().get(0).getTitle()).isEqualTo(boardList[5].getTitle()),
                () -> assertThat(myBoardList.getBoardList().get(0).getContent()).isEqualTo(boardList[5].getContent()),
                () -> assertThat(myBoardList.getBoardList().get(0).getHit()).isEqualTo(boardList[5].getHit())
        );
    }

    @Test
    @DisplayName("내가 쓴 글에서 검색 결과에 따른 게시글을 조회순으로 조회한다")
    void getMyBoardListOrderByHit() {
        // when
        CustomBoardListResponse<BoardList> myBoardList = boardRepository.getMyBoardListOrderByHit(writer.getId(), SEARCH_TYPE, SEARCH_TITLE, PAGE);

        // then
        assertAll(
                () -> assertThat(myBoardList.getBoardList().get(0).getId()).isEqualTo(boardList[9].getId()),
                () -> assertThat(myBoardList.getBoardList().get(0).getTitle()).isEqualTo(boardList[9].getTitle()),
                () -> assertThat(myBoardList.getBoardList().get(0).getContent()).isEqualTo(boardList[9].getContent()),
                () -> assertThat(myBoardList.getBoardList().get(0).getHit()).isEqualTo(boardList[9].getHit())
        );
    }
}
