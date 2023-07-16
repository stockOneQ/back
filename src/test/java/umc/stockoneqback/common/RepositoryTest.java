package umc.stockoneqback.common;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.QuerydslWebConfiguration;
import org.springframework.test.context.ActiveProfiles;
import umc.stockoneqback.global.config.JpaAuditingConfiguration;

@DataJpaTest
@Import({QuerydslWebConfiguration.class, JpaAuditingConfiguration.class})
@ActiveProfiles("test")
public class RepositoryTest {
}
