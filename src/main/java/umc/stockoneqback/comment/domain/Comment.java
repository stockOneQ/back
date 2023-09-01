package umc.stockoneqback.comment.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.user.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comment")
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;

    private String content;

    @Convert(converter = Status.StatusConverter.class)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", referencedColumnName = "id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", referencedColumnName = "id")
    private Board board;

    // 댓글 삭제시 달려있는 대댓글 모두 삭제
    @OneToMany(mappedBy = "comment", cascade = PERSIST, orphanRemoval = true)
    private final List<Reply> replyList = new ArrayList<>();

    @Builder
    private Comment (User writer, Board board, String image, String content) {
        this.writer = writer;
        this.board = board;
        this.image = image;
        this.content = content;
        this.status = Status.NORMAL;
    }

    public static Comment createComment (User writer, Board board, String image, String content) {
        return new Comment(writer, board, image, content);
    }

    public void addReply(User writer, Comment comment, String image, String content) {
        replyList.add(Reply.createReply(writer, comment, image, content));
    }

    public void updateImage(String updateImage){
        this.image = updateImage;
    }

    public void updateContent(String updateContent){
        this.content = updateContent;
    }
}
