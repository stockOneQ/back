package umc.stockoneqback.board.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.stockoneqback.board.domain.Board;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM BoardLike i WHERE i.user.id = :userId AND i.board.id = :boardId")
    void deleteByUserIdAndBoardId(@Param("userId") Long userId, @Param("boardId") Long boardId);

    boolean existsByUserIdAndBoardId(Long userId, Long boardId);

    int countByBoard(Board board);
}

