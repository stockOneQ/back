package umc.stockoneqback.friend.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Friends {
    @OneToMany(mappedBy = "resUser", cascade = CascadeType.PERSIST)
    private List<Friend> friends = new ArrayList<>();
}
