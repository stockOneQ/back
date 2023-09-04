package umc.stockoneqback.share.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.share.repository.ShareRepository;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static umc.stockoneqback.fixture.ShareFixture.*;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.WIZ;

@DisplayName("Share [Repository Layer] -> ShareRepository 테스트")
public class ShareRepositoryTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ShareRepository shareRepository;

    private User manager;
    private User supervisor;

    private Business business;

    @BeforeEach
    void setup() {
        manager = userRepository.save(ANNE.toUser());
        supervisor = userRepository.save(WIZ.toUser());

        business = businessRepository.save(Business.builder().manager(manager).supervisor(supervisor).build());

        shareRepository.save(SHARE_0.toShare(business));
        shareRepository.save(SHARE_1.toShare(business));
        shareRepository.save(SHARE_2.toShare(business));
    }

    @Test
    @DisplayName("회원 ID를 통해 해당 회원과 관련된 커넥트 게시글을 모두 삭제한다")
    void deleteByUser() {
        // when
        shareRepository.deleteByBusiness(business);

        // then
        assertThat(shareRepository.findAll().isEmpty()).isTrue();
    }
}
