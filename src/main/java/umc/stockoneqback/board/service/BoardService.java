package umc.stockoneqback.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.controller.dto.BoardResponse;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.board.domain.like.BoardLikeRepository;
import umc.stockoneqback.board.domain.views.Views;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.board.service.views.ViewsService;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {
    private final UserFindService userFindService;
    private final BoardFindService boardFindService;
    private final ViewsService viewsService;
    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    @Transactional
    public Long create(Long writerId, String title, String content){
        User writer = userFindService.findById(writerId);
        Board board = Board.createBoard(writer, title, content);

        return boardRepository.save(board).getId();
    }

    @Transactional
    public void update(Long writerId, Long boardId, String title, String content){
        validateWriter(boardId, writerId);
        Board board = boardFindService.findById(boardId);

        board.updateTitle(title);
        board.updateContent(content);
    }

    @Transactional
    public BoardResponse loadBoard(Long userId, Long boardId) {
        Board board = boardFindService.findById(boardId);
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .hit(board.getHit())
                .likes(boardLikeRepository.countByBoard(board))
                .createdDate(board.getCreatedDate())
                .writerId(board.getWriter().getLoginId())
                .writerName(board.getWriter().getName())
                .alreadyLike(AlreadyBoardLike(userId, boardId))
                .build();
    }

    @Transactional
    public void updateHit(Long userId, Long boardId) {
        if (!viewsService.existBoardIdByUserId(userId, boardId)) {
            Views views = viewsService.findById(userId).orElseGet(() -> viewsService.saveViews(userId));
            viewsService.updateViews(views, boardId);

            boardFindService.findById(boardId).updateHit();
        }
    }

    @Transactional
    public void deleteByWriter(User writer) {
        boardRepository.deleteByWriter(writer);
    }

    private void validateWriter(Long boardId, Long writerId) {
        Board board = boardFindService.findById(boardId);
        if (!board.getWriter().getId().equals(writerId)) {
            throw BaseException.type(BoardErrorCode.USER_IS_NOT_BOARD_WRITER);
        }
    }

    private boolean AlreadyBoardLike(Long userId, Long boardId) {
        return boardLikeRepository.existsByUserIdAndBoardId(userId, boardId);
    }
}
