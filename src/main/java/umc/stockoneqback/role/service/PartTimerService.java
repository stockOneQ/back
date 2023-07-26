package umc.stockoneqback.role.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.role.domain.store.PartTimer;
import umc.stockoneqback.role.domain.store.PartTimerRepository;
import umc.stockoneqback.role.exception.PartTimerErrorCode;
import umc.stockoneqback.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartTimerService {

    private final PartTimerRepository partTimerRepository;

    public PartTimer findByUser(User user) {
        return partTimerRepository.findByPartTimer(user)
                .orElseThrow(() -> BaseException.type(PartTimerErrorCode.PARTTIMER_NOT_FOUND));
    }
}
