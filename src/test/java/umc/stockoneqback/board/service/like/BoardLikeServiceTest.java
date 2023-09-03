package umc.stockoneqback.board.service.like;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.like.BoardLike;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("Board [Service Layer] -> BoardLikeService 테스트")
public class BoardLikeServiceTest extends ServiceTest {
    @Autowired
    private BoardLikeService boardLikeService;

    private User user1;
    private User user2;

    private Board board;

    @BeforeEach
    void setup() {
        user1 = userRepository.save(SAEWOO.toUser());
        user2 = userRepository.save(ANNE.toUser());

        board = boardRepository.save(BOARD_0.toBoard(user1));
    }

    @Nested
    @DisplayName("게시글좋아요 등록")
    class register {
        @Test
        @DisplayName("본인의 게시글은 좋아요를 누를 수 없다")
        void throwExceptionBySelfBoardLikeNotAllowed() {
            assertThatThrownBy(() -> boardLikeService.register(user1.getId(), board.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.SELF_BOARD_LIKE_NOT_ALLOWED.getMessage());
        }

        @Test
        @DisplayName("한 게시글에 두 번 이상 좋아요를 누를 수 없다")
        void throwExceptionByAlreadyBoardLike() {
            // given
            boardLikeService.register(user2.getId(), board.getId());

            // when - then
            assertThatThrownBy(() -> boardLikeService.register(user2.getId(), board.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.ALREADY_BOARD_LIKE.getMessage());
        }

        @Test
        @DisplayName("게시글좋아요 등록에 성공한다")
        void success() {
            // when
            Long likeBoardId = boardLikeService.register(user2.getId(), board.getId());

            // then
            BoardLike findBoardLike = boardLikeRepository.findById(likeBoardId).orElseThrow();
            assertAll(
                    () -> assertThat(boardLikeRepository.countByBoard(board)).isEqualTo(1),
                    () -> assertThat(findBoardLike.getUser().getId()).isEqualTo(user2.getId()),
                    () -> assertThat(findBoardLike.getBoard().getId()).isEqualTo(board.getId())
            );
        }
    }

    @Nested
    @DisplayName("게시글좋아요 취소")
    class cancel {
        @Test
        @DisplayName("좋아요를 누르지 않은 게시글의 좋아요는 취소할 수 없다")
        void throwExceptionByBoardLikeNotFound() {
            // when - then
            assertThatThrownBy(() -> boardLikeService.cancel(user2.getId(), board.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.BOARD_LIKE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("게시글좋아요 취소에 성공한다")
        void success() {
            // given
            boardLikeService.register(user2.getId(), board.getId());

            // when
            boardLikeService.cancel(user2.getId(), board.getId());

            // then
            assertThat(boardLikeRepository.existsByUserIdAndBoardId(user2.getId(), board.getId())).isFalse();
        }
    }

    @Test
    @DisplayName("게시글좋아요 삭제에 성공한다")
    void success() {
        // given
        boardLikeService.register(user2.getId(), board.getId());

        // when
        boardLikeService.deleteByUser(user2);

        // then
        assertThat(boardLikeRepository.existsByUserIdAndBoardId(user2.getId(), board.getId())).isFalse();
    }
}