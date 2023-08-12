package umc.stockoneqback.auth.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FcmTokenRedisRepository extends CrudRepository<FcmToken, Long> {
    @Override
    List<FcmToken> findAll();
}
