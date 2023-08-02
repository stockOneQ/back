package umc.stockoneqback.board.infra.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.infra.query.dto.BoardList;
import umc.stockoneqback.board.infra.query.dto.QBoardList;
import umc.stockoneqback.global.base.Status;

import java.util.List;

import static umc.stockoneqback.board.domain.QBoard.board;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardListQueryRepositoryImpl implements BoardListQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<BoardList> getBoardListOrderByTime() {
        return query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL))
                .orderBy(board.createdDate.desc())
                .fetch();
    }

    @Override
    public List<BoardList> getBoardListOrderByHit() {
        return query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL))
                .orderBy(board.hit.desc())
                .fetch();
    }
}
