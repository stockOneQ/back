package umc.stockoneqback.board.domain.views;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("Views")
public class Views {
    public static final Long DEFAULT_TTL = 86400L;

    @Id
    private Long id;

    private List<Long> boardIdList = new ArrayList<>();

    @TimeToLive
    private Long expiration;

    @Builder
    private Views(Long userId) {
        this.id = userId;
        this.expiration = DEFAULT_TTL;
    }

    public static Views createViews(Long userId) {
        return new Views(userId);
    }

    public void updateBoardIdList(Long boardId) {
        boardIdList.add(boardId);
    }
}
