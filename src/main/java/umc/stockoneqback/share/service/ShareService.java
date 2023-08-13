package umc.stockoneqback.share.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.share.repository.ShareRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShareService {
    private final ShareRepository shareRepository;

    @Transactional
    public void deleteShareByBusiness(Business business) {
        shareRepository.deleteByBusiness(business);
    }
}
