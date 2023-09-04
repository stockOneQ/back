package umc.stockoneqback.share.infra.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.fixture.ShareFixture;
import umc.stockoneqback.share.domain.Category;
import umc.stockoneqback.share.domain.Share;
import umc.stockoneqback.share.domain.ShareSearchType;
import umc.stockoneqback.share.infra.query.dto.CustomShareListPage;
import umc.stockoneqback.share.infra.query.dto.ShareList;
import umc.stockoneqback.share.repository.ShareRepository;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.WIZ;

@DisplayName("Share [Repository Layer] -> ShareListQueryRepository 테스트")
class ShareListQueryRepositoryImplTest extends RepositoryTest {
    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    private User manager;
    private User supervisor;

    private Business business;

    List<Share> shareList = new ArrayList<>();

    private static final Category ANNOUNCEMENT = Category.ANNOUNCEMENT;
    private static final ShareSearchType TITLE = ShareSearchType.TITLE;
    private static final String SEARCH_WORD = "제목";

    @BeforeEach
    void setUp() {
        manager = userRepository.save(ANNE.toUser());
        supervisor = userRepository.save(WIZ.toUser());

        business = businessRepository.save(new Business(manager, supervisor));

        for (ShareFixture shareFixture : ShareFixture.values()) {
            try {
                shareList.add(shareFixture.toShare(business));
                shareRepository.save(shareFixture.toShare(business));
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @DisplayName("주어진 조건에 따라 자료 글 목록을 반환한다")
    void findProductByName() {
        // given - when
        CustomShareListPage<ShareList> findShareList = shareRepository.findShareList(business.getId(), ANNOUNCEMENT, TITLE, SEARCH_WORD, 0);

        // then
        assertAll(
                () -> assertThat(findShareList.getPageInfo().getNumberOfElements()).isEqualTo(5),
                () -> assertThat(findShareList.getPageInfo().getTotalPages()).isEqualTo(1),
                () -> assertThat(findShareList.getPageInfo().getPageSize()).isEqualTo(6),
                () -> assertThat(findShareList.getPageInfo().getPageNumber()).isEqualTo(0),
                () -> assertThat(findShareList.getShareList().get(0).getTitle()).isEqualTo(shareList.get(4).getTitle()),
                () -> assertThat(findShareList.getShareList().get(1).getTitle()).isEqualTo(shareList.get(3).getTitle()),
                () -> assertThat(findShareList.getShareList().get(2).getTitle()).isEqualTo(shareList.get(2).getTitle()),
                () -> assertThat(findShareList.getShareList().get(3).getTitle()).isEqualTo(shareList.get(1).getTitle()),
                () -> assertThat(findShareList.getShareList().get(4).getTitle()).isEqualTo(shareList.get(0).getTitle())
        );
    }
}