package umc.stockoneqback.board.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.board.infra.query.BoardListQueryRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardListQueryRepository {
}
