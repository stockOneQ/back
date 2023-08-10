package umc.stockoneqback.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.stockoneqback.user.domain.User;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    boolean existsBySupervisorAndManager(User supervisor, User manager);
    Optional<Business> findBySupervisorAndManager(User supervisor, User manager);
    void deleteBySupervisorAndManager(User supervisor, User manager);
    Optional<Business> findBySupervisorIdAndManagerId(Long supervisorId, Long managerId);
}
