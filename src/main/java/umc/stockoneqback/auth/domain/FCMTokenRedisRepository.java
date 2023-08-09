package umc.stockoneqback.auth.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FCMTokenRedisRepository extends CrudRepository<FCMToken, Long> {
    @Override
    List<FCMToken> findAll();
}
