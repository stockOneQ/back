package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.user.domain.User;

@Getter
@RequiredArgsConstructor
public enum CommentFixture {
    COMMENT_0("이미지0", "댓글0"),
    COMMENT_1("이미지1", "댓글1"),
    COMMENT_2("이미지2", "댓글2")
    ;

    private final String image;
    private final String content;

    public Comment toComment(User writer, Board board){
        return Comment.createComment(writer, board, image, content);
    }
}
