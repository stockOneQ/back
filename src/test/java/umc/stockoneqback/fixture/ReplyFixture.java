package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.user.domain.User;

@Getter
@RequiredArgsConstructor
public enum ReplyFixture {
    REPLY_0(null, "대댓글0"),
    REPLY_1(null, "대댓글1"),
    REPLY_2(null, "대댓글2"),
    REPLY_3(null, "대댓글3"),
    REPLY_4(null, "대댓글4"),
    REPLY_5(null, "대댓글5"),
    REPLY_6(null, "대댓글6"),
    REPLY_7(null, "대댓글7"),
    REPLY_8(null, "대댓글8"),
    REPLY_9(null, "대댓글9")
    ;

    private final String image;
    private final String content;

    public Reply toReply(User writer, Comment comment){
        return Reply.createReply(writer, comment, image, content);
    }
}
