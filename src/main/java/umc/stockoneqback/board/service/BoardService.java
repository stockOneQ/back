package umc.stockoneqback.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.board.controller.dto.BoardResponse;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.GlobalErrorCode;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {
    private final UserFindService userFindService;
    private final BoardFindService boardFindService;
    private final BoardRepository boardRepository;
    private final FileService fileService;

    @Transactional
    public Long create(Long writerId, String title, MultipartFile file, String content){
        User writer = userFindService.findById(writerId);
        String fileUrl = null;
        if (file != null)
            fileUrl = fileService.uploadBoardFiles(file);

        Board board = Board.createBoard(writer, title ,fileUrl, content);
        return boardRepository.save(board).getId();
    }

    @Transactional
    public void update(Long writerId, Long boardId, String title, MultipartFile file, String content){
        validateWriter(boardId, writerId);
        Board board = boardFindService.findById(boardId);

        String fileUrl = null;
        if (file != null)
            fileUrl = fileService.uploadBoardFiles(file);

        board.updateTitle(title);
        board.updateFile(fileUrl);
        board.updateContent(content);
    }

    @Transactional
    public void delete(Long writerId, Long boardId){
        validateWriter(boardId, writerId);
        boardRepository.deleteById(boardId);
    }

    @Transactional
    public BoardResponse loadBoard(Long userId, Long boardId) throws IOException{
        Board board = boardFindService.findById(boardId);
        validateUser(userId);
        byte[] file = getImageOrElseNull(board.getFile());
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .file(file)
                .content(board.getContent())
                .build();
    }

    private void validateWriter(Long boardId, Long writerId) {
        Board board = boardFindService.findById(boardId);
        if (!board.getWriter().getId().equals(writerId)) {
            throw BaseException.type(BoardErrorCode.USER_IS_NOT_BOARD_WRITER);
        }
    }

    private void validateUser(Long userId) {
        User user = userFindService.findById(userId);
        if (user.getRole() == Role.SUPERVISOR)
            throw BaseException.type(GlobalErrorCode.INVALID_USER_JWT);
        else if (user.getRole() == Role.PART_TIMER) {
            throw BaseException.type(GlobalErrorCode.INVALID_USER_JWT);
        }
        else if (user.getRole() == Role.MANAGER) {
            return;
        }
    }

    private byte[] getImageOrElseNull(String file) throws IOException {
        if (file == null)
            return null;
        return fileService.downloadToResponseDto(file);
    }
}
