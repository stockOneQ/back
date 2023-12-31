package umc.stockoneqback.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.global.exception.BaseException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardFindService {
    private final BoardRepository boardRepository;

    public Board findById(Long boardId){
        return boardRepository.findById(boardId)
                .orElseThrow(() -> BaseException.type(BoardErrorCode.BOARD_NOT_FOUND));
    }
}

