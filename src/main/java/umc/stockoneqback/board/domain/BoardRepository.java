package umc.stockoneqback.board.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.board.infra.query.BoardListQueryRepository;
import umc.stockoneqback.user.domain.User;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardListQueryRepository {
    void deleteByWriter(User writer);
}
