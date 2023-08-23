package umc.stockoneqback.reply.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.service.CommentFindService;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.reply.controller.dto.ReplyListResponse;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.reply.domain.ReplyRepository;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyListService {
    private final UserFindService userFindService;
    private final CommentFindService commentFindService;
    private final ReplyRepository replyRepository;
    private final FileService fileService;

    @Transactional
    public List<ReplyListResponse> getReplyList(Long userId, Long commentId) throws IOException {
        User user = userFindService.findById(userId);
        Comment comment = commentFindService.findById(commentId);
        validateManager(user);

        List<Reply> replies = replyRepository.findReplyListOrderByTime(comment.getId());
        List<ReplyListResponse> replyLists = new ArrayList<>();

        for(Reply reply : replies){
            byte[] image = getImageOrElseNull(reply.getImage());
            ReplyListResponse replyListResponse = ReplyListResponse.builder()
                    .id(reply.getId())
                    .image(image)
                    .content(reply.getContent())
                    .createdDate(reply.getCreatedDate())
                    .writerId(reply.getWriter().getLoginId())
                    .writerName(reply.getWriter().getName())
                    .build();
            replyLists.add(replyListResponse);
        }

        return replyLists;
    }

    byte[] getImageOrElseNull(String imageUrl) throws IOException {
        if (imageUrl == null)
            return null;
        return fileService.downloadToResponseDto(imageUrl);
    }

    private void validateManager(User user) {
        if (user.getRole() != Role.MANAGER) {
            throw BaseException.type(UserErrorCode.USER_IS_NOT_MANAGER);
        }
    }
}
