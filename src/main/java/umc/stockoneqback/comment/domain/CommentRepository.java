package umc.stockoneqback.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.board.domain.Board;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    int countByBoard(Board board);
}
