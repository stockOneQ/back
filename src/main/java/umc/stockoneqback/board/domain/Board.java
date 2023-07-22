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
/*
 * TODO : 첨부파일 관련 사항 확정 시 file 컬럼 수정
 * */
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String file;

    private String content;

    private int hit;

    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", referencedColumnName = "id")
    private User writer;

    // 게시글 삭제시 달려있는 댓글 모두 삭제
    @OneToMany(mappedBy = "board", cascade = PERSIST, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    private Board (User writer, String title, String file, String content) {
        this.writer = writer;
        this.title = title;
        this.file = file;
        this.content = content;
        this.hit = 0;
        this.status = Status.NORMAL;
    }

    public static Board createBoard(User writer, String title, String file, String content) {
        return new Board(writer, title, file, content);
    }

    public void addComment(User writer, String image, String content) {
        commentList.add(Comment.createComment(writer, this, image, content));
    }

    public void updateTitle(String updateTitle){
        this.title = updateTitle;
    }

    public void updateFile(String updateFile){
        this.file = updateFile;
    }

    public void updateContent(String updateContent){
        this.content = updateContent;
    }
}
