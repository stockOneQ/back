package umc.stockoneqback.board.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.user.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board")
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private int hit;

    @Convert(converter = Status.StatusConverter.class)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", referencedColumnName = "id")
    private User writer;

    @OneToMany(mappedBy = "board", cascade = PERSIST, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    private Board (User writer, String title, String content) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.hit = 0;
        this.status = Status.NORMAL;
    }

    public static Board createBoard(User writer, String title, String content) {
        return new Board(writer, title, content);
    }

    public void addComment(User writer, String image, String content) {
        commentList.add(Comment.createComment(writer, this, image, content));
    }

    public void updateTitle(String updateTitle){
        this.title = updateTitle;
    }

    public void updateContent(String updateContent){
        this.content = updateContent;
    }

    public void updateHit(){
        this.hit = this.hit + 1;
    }
}
