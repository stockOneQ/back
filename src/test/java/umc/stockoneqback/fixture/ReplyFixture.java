package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.user.domain.User;

@Getter
@RequiredArgsConstructor
public enum ReplyFixture {
    REPLY_0("이미지0", "대댓글0"),
    REPLY_1("이미지1", "대댓글1"),
    REPLY_2("이미지2", "대댓글2")
    ;

    private final String image;
    private final String content;

    public Reply toReply(User writer, Comment comment){
        return Reply.createReply(writer, comment, image, content);
    }
}
