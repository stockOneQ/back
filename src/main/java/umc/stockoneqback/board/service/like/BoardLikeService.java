package umc.stockoneqback.board.service.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.like.BoardLike;
import umc.stockoneqback.board.domain.like.BoardLikeRepository;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.board.service.BoardFindService;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardLikeService {
    private final UserFindService userFindService;
    private final BoardFindService boardFindService;
    private final BoardLikeRepository boardLikeRepository;

    @Transactional
    public Long register(Long userId, Long boardId){
        validateSelfBoardLike(userId, boardId);
        validateAlreadyBoardLike(userId, boardId);

        User user = userFindService.findById(userId);
        Board board = boardFindService.findById(boardId);
        BoardLike likeBoard = BoardLike.registerBoardLike(user, board);

        return boardLikeRepository.save(likeBoard).getId();
    }

    @Transactional
    public void deleteByUser(User user) {
        boardLikeRepository.deleteByUser(user);
    }

    private void validateSelfBoardLike(Long userId, Long boardId) {
        Board board = boardFindService.findById(boardId);
        if (board.getWriter().getId().equals(userId)) {
            throw BaseException.type(BoardErrorCode.SELF_BOARD_LIKE_NOT_ALLOWED);
        }
    }

    private void validateAlreadyBoardLike(Long userId, Long boardId) {
        if (boardLikeRepository.existsByUserIdAndBoardId(userId, boardId)) {
            throw BaseException.type(BoardErrorCode.ALREADY_BOARD_LIKE);
        }
    }

    @Transactional
    public void cancel(Long userId, Long boardId){
        validateCancel(userId, boardId);
        boardLikeRepository.deleteByUserIdAndBoardId(userId, boardId);
    }

    private void validateCancel(Long userId, Long boardId) {
        if (!boardLikeRepository.existsByUserIdAndBoardId(userId, boardId)) {
            throw BaseException.type(BoardErrorCode.BOARD_LIKE_NOT_FOUND);
        }
    }
}
