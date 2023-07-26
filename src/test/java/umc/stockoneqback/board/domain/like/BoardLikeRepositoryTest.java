package umc.stockoneqback.board.domain.like;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("Board [Repository Layer] -> BoardLikeRepository 테스트")
public class BoardLikeRepositoryTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardLikeRepository boardLikeRepository;

    private User user1;
    private User user2;
    private Board board;

    @BeforeEach
    void setup() {
        user1 = userRepository.save(SAEWOO.toUser());
        user2 = userRepository.save(ANNE.toUser());
        board = boardRepository.save(BOARD_0.toBoard(user1));
    }

    @Test
    @DisplayName("좋아요를 누른 회원 ID와 좋아요가 눌린 게시글 ID를 이용하여 게시글좋아요 정보가 존재하는지 확인한다")
    void existsByUserIdAndAndBoardId() {
        // given
        boardLikeRepository.save(BoardLike.registerBoardLike(user1, board));

        // when
        boolean actual1 = boardLikeRepository.existsByUserIdAndBoardId(user1.getId(), board.getId());
        boolean actual2 = boardLikeRepository.existsByUserIdAndBoardId(user2.getId(), board.getId());

        // then
        Assertions.assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("좋아요를 누른 회원 ID와 좋아요가 눌린 게시글 ID를 이용하여 게시글좋아요 정보를 삭제한다")
    void deleteByFollowingIdAndFollowerId() {
        // given
        boardLikeRepository.save(BoardLike.registerBoardLike(user1, board));

        // when
        boardLikeRepository.deleteByUserIdAndBoardId(user1.getId(), board.getId());

        // then
        assertThat(boardLikeRepository.existsByUserIdAndBoardId(user1.getId(), board.getId())).isFalse();
    }

    @Test
    @DisplayName("게시글에 달린 좋아요 수를 확인한다")
    void countByBoard() {
        // given
        boardLikeRepository.save(BoardLike.registerBoardLike(user1, board));
        boardLikeRepository.save(BoardLike.registerBoardLike(user2, board));

        // when - then
        assertThat(boardLikeRepository.countByBoard(board)).isEqualTo(2L);
    }
}

