package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.admin.domain.StaticFA;
import umc.stockoneqback.admin.domain.StaticFARedisRepository;
import umc.stockoneqback.user.service.dto.response.GetFAResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFAService {
    private final StaticFARedisRepository staticFARedisRepository;

    @Transactional
    public List<GetFAResponse> getFA() {
        List<StaticFA> staticFAList = staticFARedisRepository.findAll();
        List<GetFAResponse> getFAResponseList = new ArrayList<>();
        for (StaticFA staticFA: staticFAList) {
            getFAResponseList.add(GetFAResponse.builder()
                                                .question(staticFA.getId())
                                                .answer(staticFA.getAnswer())
                                                .build());
        }
        return getFAResponseList;
    }
}
