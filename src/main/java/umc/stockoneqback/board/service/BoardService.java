package umc.stockoneqback.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {
    private final UserFindService userFindService;
    private final BoardFindService boardFindService;
    private final BoardRepository boardRepository;

    @Transactional
    public Long create(Long writerId, String title, String file, String content){
        User writer = userFindService.findById(writerId);
        Board board = Board.createBoard(writer, title ,file, content);

        return boardRepository.save(board).getId();
    }

    @Transactional
    public void update(Long writerId, Long boardId, String title, String file, String content){
        validateWriter(boardId, writerId);
        Board board = boardFindService.findById(boardId);

        board.updateTitle(title);
        board.updateFile(file);
        board.updateContent(content);
    }

    @Transactional
    public void delete(Long writerId, Long boardId){
        validateWriter(boardId, writerId);
        boardRepository.deleteById(boardId);
    }

    private void validateWriter(Long boardId, Long writerId) {
        Board board = boardFindService.findById(boardId);
        if (!board.getWriter().getId().equals(writerId)) {
            throw BaseException.type(BoardErrorCode.USER_IS_NOT_BOARD_WRITER);
        }
    }
}
