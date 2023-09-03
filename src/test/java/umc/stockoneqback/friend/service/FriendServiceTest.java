package umc.stockoneqback.friend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.exception.FriendErrorCode;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.UNKNOWN;

@DisplayName("Friend [Service Layer] -> FriendService 테스트")
class FriendServiceTest extends ServiceTest {
    @Autowired
    private FriendService friendService;

    @Autowired
    private UserFindService userFindService;

    private Long senderId;
    private Long receiverId;

    @BeforeEach
    void setUp() {
        senderId = userRepository.save(ANNE.toUser()).getId();
        receiverId = userRepository.save(UNKNOWN.toUser()).getId();
    }

    @Nested
    @DisplayName("점주님 - 점주님 Friend 신청")
    class requestFriend {
        @Test
        @DisplayName("본인과는 친구 신청에 실패한다")
        void throwExceptionBySelfFriendRequestNotAllowed() {
            // when - then
            assertThatThrownBy(() -> friendService.requestFriend(senderId, senderId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.SELF_FRIEND_REQUEST_NOT_ALLOWED.getMessage());
        }

        @Test
        @DisplayName("이미 친구라면 신청에 실패한다")
        void throwExceptionByAlreadyExistFriend() {
            // given
            friendService.requestFriend(senderId, receiverId);

            // when - then
            assertThatThrownBy(() -> friendService.requestFriend(senderId, receiverId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.ALREADY_EXIST_FRIEND.getMessage());
        }

        @Test
        @DisplayName("점주님 - 점주님 Friend 신청에 성공한다")
        void success() {
            // given
            friendService.requestFriend(senderId, receiverId);

            // when
            User sender = userFindService.findById(senderId);
            User receiver = userFindService.findById(receiverId);
            Friend findFriend = friendRepository.findBySenderIdAndReceiverId(senderId, receiverId).orElseThrow();

            // then
            assertAll(
                    () -> assertThat(findFriend.getSender()).isEqualTo(sender),
                    () -> assertThat(findFriend.getReceiver()).isEqualTo(receiver),
                    () -> assertThat(findFriend.getRelationStatus()).isEqualTo(RelationStatus.REQUEST)
            );
        }
    }

    @Nested
    @DisplayName("점주님 - 점주님 Friend 요청 취소")
    class cancelFriend {
        @Test
        @DisplayName("Friend가 존재하지 않는다면 Friend 취소에 실패한다")
        void throwExceptionByFriendNotFound() {
            // when - then
            assertThatThrownBy(() -> friendService.cancelFriend(senderId, receiverId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.FRIEND_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("이미 수락된 상태라면 취소에 실패한다")
        void throwExceptionByStatusIsAccept() {
            // given
            friendService.requestFriend(senderId, receiverId);

            // when
            friendService.acceptFriend(senderId, receiverId);

            // when - then
            assertThatThrownBy(() -> friendService.cancelFriend(senderId, receiverId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.STATUS_IS_ACCEPT.getMessage());
        }

        @Test
        @DisplayName("점주님 - 점주님 Friend 취소에 성공한다")
        void success() {
            // given
            friendService.requestFriend(senderId, receiverId);

            // when
            friendService.cancelFriend(senderId, receiverId);

            // then
            User sender = userFindService.findById(senderId);
            User receiver = userFindService.findById(receiverId);
            assertThat(friendRepository.existsBySenderAndReceiver(sender, receiver)).isFalse();
        }
    }

    @Nested
    @DisplayName("점주님 - 점주님 Friend 요청 수락")
    class acceptFriend {
        @Test
        @DisplayName("Friend가 존재하지 않는다면 Friend 수락에 실패한다")
        void throwExceptionByFriendNotFound() {
            // when - then
            assertThatThrownBy(() -> friendService.acceptFriend(senderId, receiverId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.FRIEND_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("이미 수락된 상태라면 요청에 실패한다")
        void throwExceptionByStatusIsAccept() {
            // given
            friendService.requestFriend(senderId, receiverId);

            // when
            friendService.acceptFriend(senderId, receiverId);

            // when - then
            assertThatThrownBy(() -> friendService.acceptFriend(senderId, receiverId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.STATUS_IS_ACCEPT.getMessage());
        }

        @Test
        @DisplayName("점주님 - 점주님 Friend 수락에 성공한다")
        void success() {
            // given
            friendService.requestFriend(senderId, receiverId);

            // when
            Long acceptFriendId = friendService.acceptFriend(senderId, receiverId);
            Friend findFriend = friendRepository.findById(acceptFriendId).orElseThrow();

            // then
            User sender = userFindService.findById(senderId);
            User receiver = userFindService.findById(receiverId);
            assertAll(
                    () -> assertThat(findFriend.getSender()).isEqualTo(sender),
                    () -> assertThat(findFriend.getReceiver()).isEqualTo(receiver),
                    () -> assertThat(findFriend.getRelationStatus()).isEqualTo(RelationStatus.ACCEPT)
            );
        }
    }

    @Nested
    @DisplayName("점주님 - 점주님 Friend 요청 거절")
    class rejectFriend {
        @Test
        @DisplayName("Friend가 존재하지 않는다면 Friend 거절에 실패한다")
        void throwExceptionByFriendNotFound() {
            // when - then
            assertThatThrownBy(() -> friendService.rejectFriend(senderId, receiverId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.FRIEND_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("이미 수락된 상태라면 거절에 실패한다")
        void throwExceptionByStatusIsAccept() {
            // given
            friendService.requestFriend(senderId, receiverId);

            // when
            friendService.acceptFriend(senderId, receiverId);

            // when - then
            assertThatThrownBy(() -> friendService.rejectFriend(senderId, receiverId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.STATUS_IS_ACCEPT.getMessage());
        }

        @Test
        @DisplayName("점주님 - 점주님 Friend 거절에 성공한다")
        void success() {
            // given
            friendService.requestFriend(senderId, receiverId);

            // when
            friendService.rejectFriend(senderId, receiverId);

            // then
            User sender = userFindService.findById(senderId);
            User receiver = userFindService.findById(receiverId);
            assertThat(friendRepository.existsBySenderAndReceiver(sender, receiver)).isFalse();
        }
    }

    @Nested
    @DisplayName("점주님 - 점주님 Friend 삭제")
    class deleteFriend {
        @Test
        @DisplayName("Friend가 존재하지 않는다면 Friend 삭제에 실패한다")
        void throwExceptionByFriendNotFound() {
            // when - then
            assertThatThrownBy(() -> friendService.deleteFriend(senderId, receiverId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.FRIEND_NOT_FOUND.getMessage());
            assertThatThrownBy(() -> friendService.deleteFriend(receiverId, senderId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.FRIEND_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("아직 친구 요청 중인 상태라면 삭제에 실패한다")
        void throwExceptionByStatusIsRequest() {
            // given
            friendService.requestFriend(senderId, receiverId);

            // when - then
            assertThatThrownBy(() -> friendService.deleteFriend(senderId, receiverId))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FriendErrorCode.STATUS_IS_REQUEST.getMessage());
        }

        @Test
        @DisplayName("점주님 - 점주님 Friend 삭제에 성공한다")
        void success() {
            // given
            friendService.requestFriend(senderId, receiverId);
            friendService.acceptFriend(senderId, receiverId);

            // when
            friendService.deleteFriend(receiverId, senderId);

            // then
            User user = userFindService.findById(receiverId);
            User friendUser = userFindService.findById(senderId);
            assertThat(friendRepository.existsBySenderAndReceiver(user, friendUser)).isFalse();
        }
    }

    @Test
    @DisplayName("현재 사용자의 Friend 전체 삭제에 성공한다")
    void deleteAll() {
        // given
        User user = userFindService.findById(senderId);
        friendService.requestFriend(senderId, receiverId);
        friendService.acceptFriend(senderId, receiverId);

        // when
        friendService.deleteFriendByUser(user);

        // then
        assertThat(friendRepository.findById(senderId).isEmpty()).isTrue();
    }
}