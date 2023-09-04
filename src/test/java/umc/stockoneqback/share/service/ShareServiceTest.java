package umc.stockoneqback.share.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.share.controller.dto.ShareRequest;
import umc.stockoneqback.share.domain.Category;
import umc.stockoneqback.share.domain.Share;
import umc.stockoneqback.share.exception.ShareErrorCode;
import umc.stockoneqback.user.domain.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static umc.stockoneqback.fixture.ShareFixture.SHARE_0;
import static umc.stockoneqback.fixture.ShareFixture.SHARE_9;
import static umc.stockoneqback.fixture.StoreFixture.B_CHICKEN;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Share [Service Layer] -> ShareService 테스트")
class ShareServiceTest extends ServiceTest {
    @Autowired
    private ShareService shareService;

    private User supervisor;
    private User manager1;
    private User manager2;

    private Store store;

    private Business business1;
    private Business business2;

    private Share share1;
    private Share share2;
    private Share share3;

    private ShareRequest shareRequest;

    private static final String CATEGORY = "공지사항";
    private static final String INVALID_CATEGORY = "공지";

    private final List<Long> selectedShareId = new ArrayList<>();

    @BeforeEach
    void setup() {
        supervisor = userRepository.save(WIZ.toUser());

        manager1 = userRepository.save(UNKNOWN.toUser());
        manager2 = userRepository.save(ANNE.toUser());

        store = storeRepository.save(B_CHICKEN.toStore(manager1));

        business1 = businessRepository.save(new Business(manager1, supervisor));
        business2 = businessRepository.save(Business.builder().supervisor(supervisor).manager(manager2).build());

        share1 = shareRepository.save(new Share("title", "share/filepath", "content", Category.ETC, business1));
        share2 = shareRepository.save(SHARE_0.toShare(business2));
        share3 = shareRepository.save(SHARE_9.toShare(business2));

        shareRequest = new ShareRequest("share test title", "share test content");
        selectedShareId.add(share1.getId());
    }

    @Nested
    @DisplayName("게시글 등록")
    class create {
        @Test
        @DisplayName("유효한 카테고리가 아니라면 게시글을 등록할 수 없다")
        void throwExceptionByNotFoundCategory() {
            // when - then
            assertThatThrownBy(() -> shareService.create(supervisor.getId(), business1.getId(), INVALID_CATEGORY, shareRequest, null))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ShareErrorCode.NOT_FOUND_CATEGORY.getMessage());
        }

        @Test
        @DisplayName("게시글 등록에 성공한다")
        void success() {
            // given - when
            shareService.create(supervisor.getId(), business1.getId(), CATEGORY, shareRequest, null);

            // then
            Share findShare = shareRepository.findAll().get(3);
            assertAll(
                    () -> assertThat(findShare.getTitle()).isEqualTo(shareRequest.title()),
                    () -> assertThat(findShare.getFile()).isEqualTo(null),
                    () -> assertThat(findShare.getContent()).isEqualTo(shareRequest.content()),
                    () -> assertThat(findShare.getBusiness()).isEqualTo(business1),
                    () -> assertThat(findShare.getBusiness().getSupervisor()).isEqualTo(supervisor)
            );
        }
    }

    @Nested
    @DisplayName("게시글 수정")
    class update {
        @Test
        @DisplayName("작성자가 아니라면 게시글을 수정할 수 없다")
        void throwExceptionByNotAWriter() {
            // when - then
            assertThatThrownBy(() -> shareService.update(manager1.getId(), share1.getId(), shareRequest, null))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ShareErrorCode.NOT_A_WRITER.getMessage());
        }

        @Test
        @DisplayName("게시글 수정에 성공한다")
        void success() {
            // given
            shareService.update(supervisor.getId(), share1.getId(), shareRequest, null);

            // when
            Share findShare = shareRepository.findById(share1.getId()).orElseThrow();

            // then
            assertAll(
                    () -> assertThat(findShare.getTitle()).isEqualTo(shareRequest.title()),
                    () -> assertThat(findShare.getFile()).isEqualTo(share1.getFile()),
                    () -> assertThat(findShare.getContent()).isEqualTo(shareRequest.content())
            );
        }
    }

    @Nested
    @DisplayName("게시글 상세조회")
    class detail {
        @Test
        @DisplayName("게시글 상세조회에 성공한다")
        void success() {
            // when - then
            assertDoesNotThrow(() -> shareService.detail(manager1.getId(), share1.getId()));
        }
    }

    @Nested
    @DisplayName("게시글 삭제")
    class delete {
        @Test
        @DisplayName("다른 사람의 게시글은 삭제할 수 없다")
        void throwExceptionByNotAWriter() {
            // when - then
            assertThatThrownBy(() -> shareService.delete(manager1.getId(), selectedShareId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ShareErrorCode.NOT_A_WRITER.getMessage());
        }

        @Test
        @DisplayName("게시글 삭제에 성공한다")
        void success() {
            // given
            shareService.delete(supervisor.getId(), selectedShareId);

            // when - then
            assertThatThrownBy(() -> shareService.findShareById(share1.getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ShareErrorCode.SHARE_NOT_FOUND.getMessage());
        }
    }

    @Test
    @DisplayName("현재 비즈니스의 Share 전체 삭제에 성공한다")
    void deleteAll() {
        // when
        shareService.deleteShareByBusiness(business2);

        // then
        assertAll(
                () -> assertThat(shareRepository.findById(share2.getId()).isEmpty()).isTrue(),
                () -> assertThat(shareRepository.findById(share3.getId()).isEmpty()).isTrue()
        );
    }
}
