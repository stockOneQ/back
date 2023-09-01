package umc.stockoneqback.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.service.BoardFindService;
import umc.stockoneqback.comment.controller.dto.CommentListResponse;
import umc.stockoneqback.comment.controller.dto.CustomCommentListResponse;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.domain.CommentRepository;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.reply.controller.dto.ReplyListResponse;
import umc.stockoneqback.reply.service.ReplyListService;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentListService {
    private final BoardFindService boardFindService;
    private final CommentRepository commentRepository;
    private final UserFindService userFindService;
    private final FileService fileService;
    private final ReplyListService replyListService;
    private static final Integer PAGE_SIZE = 20; // 대댓글 포함

    @Transactional
    public CustomCommentListResponse getCommentList(Long userId, Long boardId, int page) throws IOException {
        User user = userFindService.findById(userId);
        Board board = boardFindService.findById(boardId);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Comment> comments = commentRepository.findCommentListOrderByTime(board.getId() ,pageable);
        List<CommentListResponse> commentLists = new ArrayList<>();

        for(Comment comment : comments){
            byte[] image = getImageOrElseNull(comment.getImage());
            List<ReplyListResponse> replyLists = replyListService.getReplyList(user.getId(), comment.getId());
            CommentListResponse commentListResponse = CommentListResponse.builder()
                    .id(comment.getId())
                    .image(image)
                    .content(comment.getContent())
                    .createdDate(comment.getCreatedDate())
                    .writerId(comment.getWriter().getLoginId())
                    .writerName(comment.getWriter().getName())
                    .replyList(replyLists)
                    .build();
            commentLists.add(commentListResponse);
        }

        CustomCommentListResponse.CustomPageable pageInfo = new CustomCommentListResponse.CustomPageable(
                comments.getTotalPages(),
                comments.getTotalElements(),
                comments.hasNext(),
                comments.getNumberOfElements()
        );

        return new CustomCommentListResponse(pageInfo, commentLists);
    }

    byte[] getImageOrElseNull(String imageUrl) throws IOException {
        if (imageUrl == null)
            return null;
        return fileService.downloadToResponseDto(imageUrl);
    }
}
