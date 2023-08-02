//package umc.stockoneqback.board.infra.query;
//
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import umc.stockoneqback.board.domain.Board;
//import umc.stockoneqback.board.domain.BoardRepository;
//import umc.stockoneqback.board.infra.query.dto.BoardList;
//import umc.stockoneqback.common.RepositoryTest;
//import umc.stockoneqback.user.domain.User;
//import umc.stockoneqback.user.domain.UserRepository;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static umc.stockoneqback.fixture.BoardFixture.*;
//import static umc.stockoneqback.fixture.UserFixture.ANNE;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@DisplayName("Board [Repository Layer] -> BoardListQueryRepository 테스트")
//class BoardListQueryRepositoryImplTest extends RepositoryTest {
//    @Autowired
//    private BoardRepository boardRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    private final Board[] boardList = new Board[10];
//    private User writer;
//
//    @BeforeEach
//    void setUp() {
//        boardRepository.deleteAll();
//        writer = userRepository.save(ANNE.toUser());
//
//        boardList[0] = boardRepository.save(BOARD_0.toBoard(writer));
//        boardList[1] = boardRepository.save(BOARD_1.toBoard(writer));
//        boardList[2] = boardRepository.save(BOARD_2.toBoard(writer));
//        boardList[3] = boardRepository.save(BOARD_3.toBoard(writer));
//        boardList[4] = boardRepository.save(BOARD_4.toBoard(writer));
//        boardList[5] = boardRepository.save(BOARD_5.toBoard(writer));
//        boardList[6] = boardRepository.save(BOARD_6.toBoard(writer));
//        boardList[7] = boardRepository.save(BOARD_7.toBoard(writer));
//        boardList[8] = boardRepository.save(BOARD_8.toBoard(writer));
//        boardList[9] = boardRepository.save(BOARD_9.toBoard(writer));
//    }
//
//    @AfterEach
//    void clearAll() {
//        boardRepository.deleteAll();
//    }
//
//    @Nested
//    @DisplayName("게시글 목록")
//    class boardList {
//        @Order(1)
//        @Test
//        @DisplayName("게시글을 최신순으로 조회한다")
//        void getBoardListOrderByTime() {
//            // when
//            List<BoardList> boardListOrderByTime = boardRepository.getBoardListOrderByTime();
//
//            // then
//            assertAll(
//                    () -> assertThat(boardListOrderByTime.get(9).getId()).isEqualTo(boardList[0].getId()),
//                    () -> assertThat(boardListOrderByTime.get(9).getTitle()).isEqualTo(boardList[0].getTitle()),
//                    () -> assertThat(boardListOrderByTime.get(9).getContent()).isEqualTo(boardList[0].getContent()),
//                    () -> assertThat(boardListOrderByTime.get(9).getHit()).isEqualTo(boardList[0].getHit())
//            );
//        }
//
//        @Order(2)
//        @Test
//        @DisplayName("게시글을 조회순으로 조회한다")
//        void getBoardListOrderByHit() {
//            // when
//            List<BoardList> boardListOrderByHit = boardRepository.getBoardListOrderByHit();
//
//            // then
//            for (int i = 0; i < boardListOrderByHit.size(); i++) {
//                BoardList boardOrderByHit = boardListOrderByHit.get(i);
//                Board board = boardList[i];
//
//                assertAll(
//                        () -> assertThat(boardOrderByHit.getId()).isEqualTo(board.getId()),
//                        () -> assertThat(boardOrderByHit.getTitle()).isEqualTo(board.getTitle()),
//                        () -> assertThat(boardOrderByHit.getContent()).isEqualTo(board.getContent()),
//                        () -> assertThat(boardOrderByHit.getHit()).isEqualTo(board.getHit())
//                );
//            }
//        }
//    }
//}