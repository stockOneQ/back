package umc.stockoneqback.reply.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.global.BaseTimeEntity;
import umc.stockoneqback.global.Status;
import umc.stockoneqback.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reply")
public class Reply extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;

    private String content;

    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", referencedColumnName = "id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment;

    @Builder
    private Reply (User writer, Comment comment, String image, String content) {
        this.writer = writer;
        this.comment = comment;
        this.image = image;
        this.content = content;
        this.status = Status.NORMAL;
    }

    public static Reply createReply (User writer, Comment comment, String image, String content) {
        return new Reply(writer, comment, image, content);
    }
}
