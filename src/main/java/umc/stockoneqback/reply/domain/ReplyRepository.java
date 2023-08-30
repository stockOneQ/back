package umc.stockoneqback.reply.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.user.domain.User;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // @Query
    @Query(value = "SELECT r.* FROM reply r WHERE r.comment_id = :commentId AND r.status = '정상' order by r.created_date asc ", nativeQuery = true)
    List<Reply> findReplyListOrderByTime(@Param("commentId") Long commentId);

    // Query Method
    int countByComment(Comment comment);
    void deleteByWriter(User writer);
}
