package umc.stockoneqback.admin.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StaticFARedisRepository extends CrudRepository<StaticFA, String> {
    @Override
    List<StaticFA> findAll();
}
