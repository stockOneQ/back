package umc.stockoneqback.role.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.role.domain.store.PartTimer;
import umc.stockoneqback.role.domain.store.PartTimerRepository;
import umc.stockoneqback.role.domain.store.PartTimers;
import umc.stockoneqback.role.exception.PartTimerErrorCode;
import umc.stockoneqback.user.domain.User;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartTimerService {
    private final StoreService storeService;
    private final PartTimerRepository partTimerRepository;

    @Transactional
    public void savePartTimer(PartTimer partTimer) {
        partTimerRepository.save(partTimer);
    }

    public PartTimer findByUser(User user) {
        return partTimerRepository.findByPartTimer(user)
                .orElseThrow(() -> BaseException.type(PartTimerErrorCode.PARTTIMER_NOT_FOUND));
    }

    @Transactional
    public void deleteByUser(User user) {
        Optional<PartTimer> partTimer = partTimerRepository.findByPartTimer(user);
        if (partTimer.isEmpty())
            return;

        storeService.deletePartTimersByPartTimer(partTimer.get().getStore(), partTimer.get());
        partTimerRepository.deleteByPartTimer(user);
    }
}
