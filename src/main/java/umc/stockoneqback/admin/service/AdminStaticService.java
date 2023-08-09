package umc.stockoneqback.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.admin.domain.StaticFA;
import umc.stockoneqback.admin.domain.StaticFARedisRepository;
import umc.stockoneqback.admin.dto.request.AddFARequest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.GlobalErrorCode;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminStaticService {
    private final UserFindService userFindService;
    private final StaticFARedisRepository staticFARedisRepository;
    @Transactional
    public void addFA(Long userId, AddFARequest addFARequest) {
        isAdministrator(userId);
        for (AddFARequest.AddFAKeyValue addFAKeyValue : addFARequest.addFAKeyValueList()) {
            staticFARedisRepository.save(StaticFA.createStaticFa(addFAKeyValue.question(), addFAKeyValue.answer()));
        }
    }

    @Transactional
    public void deleteFA(Long userId, String question) {
        isAdministrator(userId);
        staticFARedisRepository.deleteById(question);
    }

    private void isAdministrator(Long userId) {
        User user = userFindService.findById(userId);
        if (user.getRole() == Role.ADMINISTRATOR)
            return;
        throw BaseException.type(GlobalErrorCode.NOT_FOUND);
    }
}
