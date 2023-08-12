package umc.stockoneqback.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.share.domain.Share;

public interface ShareRepository extends JpaRepository<Share, Long> {
    void deleteByBusiness(Business business);
}
