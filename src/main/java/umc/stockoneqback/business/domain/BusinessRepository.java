package umc.stockoneqback.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.business.infra.query.BusinessFindQueryRepository;
import umc.stockoneqback.user.domain.User;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long>, BusinessFindQueryRepository {
    boolean existsBySupervisorAndManager(User supervisor, User manager);
    Optional<Business> findBySupervisorAndManager(User supervisor, User manager);
    void deleteBySupervisorAndManager(User supervisor, User manager);
    Optional<Business> findByManagerIdAndSupervisorId(Long managerId, Long supervisorId);
}
