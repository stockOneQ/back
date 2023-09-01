package umc.stockoneqback.comment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.user.domain.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // @Query
    @Query(value = "SELECT c.* FROM comment c WHERE c.board_id = :boardId AND c.status = '정상' order by c.created_date asc ", nativeQuery = true)
    Page<Comment> findCommentListOrderByTime(@Param("boardId") Long boardId, Pageable pageable);

    // Query Method
    List<Comment> findAllByBoard(Board board);
    int countByBoard(Board board);
    void deleteByWriter(User writer);
}
