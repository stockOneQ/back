package umc.stockoneqback.admin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash("staticFa")
@AllArgsConstructor
@Builder
public class StaticFA {
    @Id
    private String id;

    @Indexed
    private String answer;

    public static StaticFA createStaticFa(String question, String answer) {
        return StaticFA.builder()
                .id(question)
                .answer(answer)
                .build();
    }
}
