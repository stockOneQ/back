package umc.stockoneqback.role.domain.store;

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
public class PartTimers {
    @OneToMany(mappedBy = "store", cascade = CascadeType.PERSIST)
    private List<PartTimer> partTimers = new ArrayList<>();

    public static PartTimers createPartTimers() {
        return new PartTimers();
    }

    public void addPartTimer(PartTimer partTimer) {
        this.partTimers.add(partTimer);
    }
}
