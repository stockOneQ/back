package umc.stockoneqback.reply.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.user.domain.User;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    int countByComment(Comment comment);

    void deleteByWriter(User writer);
}
