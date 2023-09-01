package umc.stockoneqback.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.admin.domain.StaticFA;
import umc.stockoneqback.admin.domain.StaticFARedisRepository;
import umc.stockoneqback.admin.dto.request.AddFARequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminStaticService {
    private final StaticFARedisRepository StaticFARedisRepository;

    @Transactional
    public void addFA(AddFARequest addFARequest) {
        for (AddFARequest.AddFAKeyValue addFAKeyValue : addFARequest.addFAKeyValueList()) {
            StaticFARedisRepository.save(StaticFA.createStaticFa(addFAKeyValue.question(), addFAKeyValue.answer()));
        }
    }

    @Transactional
    public void deleteFA(String question) {
        StaticFARedisRepository.deleteById(question);
    }
}
