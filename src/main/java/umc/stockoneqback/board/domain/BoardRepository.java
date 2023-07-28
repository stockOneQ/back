package umc.stockoneqback.board.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query(value = "SELECT * FROM Board b WHERE b.status = 0 " +
            "AND ((:createdDate is null AND :id is null) OR (b.created_date < :createdDate OR b.created_date = :createdDate AND b.id < :id)) " +
            "ORDER BY b.created_date DESC, b.id DESC LIMIT :pageSize", nativeQuery = true)
    List<Board> getBoardListOrderByTime(@Param("id") Long id,
                                     @Param("createdDate") LocalDateTime createdDate,
                                     @Param("pageSize") Integer pageSize);

    /**
    쿼리 수정 필요
     */
    @Query(value = "SELECT b.* FROM Board b WHERE b.status = 0 " +
            "AND ((:hit is null AND :id is null) OR (b.hit <= :hit OR b.hit = :hit AND b.id < :id)) " +
            "ORDER BY b.hit, b.id DESC LIMIT :pageSize", nativeQuery = true)
    List<Board> getBoardListOrderByHit(@Param("id") Long id,
                                    @Param("hit") int hit,
                                    @Param("pageSize") Integer pageSize);
}
