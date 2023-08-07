package umc.stockoneqback.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.share.domain.Share;

public interface ShareRepository extends JpaRepository<Share, Long> {
}
