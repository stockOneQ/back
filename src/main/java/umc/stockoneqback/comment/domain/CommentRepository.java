package umc.stockoneqback.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.user.domain.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    int countByBoard(Board board);

    List<Comment> findAllByBoard(Board board);

    void deleteByWriter(User writer);
}
