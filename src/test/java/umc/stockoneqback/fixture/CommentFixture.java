package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.user.domain.User;

@Getter
@RequiredArgsConstructor
public enum CommentFixture {
    COMMENT_0(null, "댓글0"),
    COMMENT_1(null, "댓글1"),
    COMMENT_2(null, "댓글2"),
    COMMENT_3(null, "댓글3"),
    COMMENT_4(null, "댓글4"),
    COMMENT_5(null, "댓글5"),
    COMMENT_6(null, "댓글6"),
    COMMENT_7(null, "댓글7"),
    COMMENT_8(null, "댓글8"),
    COMMENT_9(null, "댓글9"),
    COMMENT_10(null, "댓글10"),
    COMMENT_11(null, "댓글11"),
    COMMENT_12(null, "댓글12"),
    COMMENT_13(null, "댓글13"),
    COMMENT_14(null, "댓글14")
    ;

    private final String image;
    private final String content;

    public Comment toComment(User writer, Board board){
        return Comment.createComment(writer, board, image, content);
    }
}
