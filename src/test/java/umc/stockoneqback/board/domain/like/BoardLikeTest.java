package umc.stockoneqback.board.domain.like;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.fixture.BoardFixture;
import umc.stockoneqback.fixture.UserFixture;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BoardLike 도메인 테스트")
public class BoardLikeTest {
    @Test
    @DisplayName("BoardLike를 생성한다")
    void registerFollow() {
        User user = UserFixture.SAEWOO.toUser();
        Board board = BoardFixture.BOARD_0.toBoard(user);

        BoardLike boardLike = BoardLike.registerBoardLike(user, board);
        Assertions.assertAll(
                () -> assertThat(boardLike.getUser()).isEqualTo(user),
                () -> assertThat(boardLike.getBoard()).isEqualTo(board)
        );
    }
}

