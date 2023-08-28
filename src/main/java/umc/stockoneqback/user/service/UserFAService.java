package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.admin.domain.StaticFA;
import umc.stockoneqback.admin.domain.StaticFARedisRepository;
import umc.stockoneqback.user.service.dto.response.GetFAResponse;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFAService {
    private final StaticFARedisRepository StaticFARedisRepository;

    @Transactional
    public List<GetFAResponse> getFA() {
        return StaticFARedisRepository.findAll()
                .stream()
                .sorted(comparing(StaticFA::getId))
                .map(staticFA -> GetFAResponse.builder()
                        .question(staticFA.getId())
                        .answer(staticFA.getAnswer())
                        .build())
                .collect(Collectors.toList());
    }
}
