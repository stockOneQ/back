package umc.stockoneqback.share.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.share.domain.Share;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.ShareFixture.SHARE_0;
import static umc.stockoneqback.fixture.ShareFixture.SHARE_9;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.WIZ;

@DisplayName("Share [Service Layer] -> ShareService 테스트")
public class ShareServiceTest extends ServiceTest {
    @Autowired
    private ShareService shareService;

    private User manager;
    private User supervisor;
    private Business business;
    private Share shareOne;
    private Share shareTwo;

    @BeforeEach
    void setup() {
        supervisor = userRepository.save(WIZ.toUser());
        manager = userRepository.save(ANNE.toUser());
        business = businessRepository.save(Business.builder().supervisor(supervisor).manager(manager).build());
        shareOne = shareRepository.save(SHARE_0.toShare(business));
        shareTwo = shareRepository.save(SHARE_9.toShare(business));
    }

    @Test
    @DisplayName("현재 비즈니스의 Share 전체 삭제에 성공한다")
    void deleteAll() {
        // when
        shareService.deleteShareByBusiness(business);

        // then
        assertAll(
                () -> assertThat(shareRepository.findById(shareOne.getId()).isEmpty()).isTrue(),
                () -> assertThat(shareRepository.findById(shareTwo.getId()).isEmpty()).isTrue()
        );
    }
}
