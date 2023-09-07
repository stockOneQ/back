package umc.stockoneqback.friend.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Friend [Repository Layer] -> FriendRepository 테스트")
public class FriendRepositoryTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    private User user;
    private User friendUser1;
    private User friendUser2;

    @BeforeEach
    void setup() {
        user = userRepository.save(ANNE.toUser());

        friendUser1 = userRepository.save(ELLA.toUser());
        friendUser2 = userRepository.save(MIKE.toUser());

        friendRepository.save(Friend.createFriend(user, friendUser1, RelationStatus.ACCEPT));
        friendRepository.save(Friend.createFriend(user, friendUser2, RelationStatus.ACCEPT));
    }

    @Test
    @DisplayName("회원 ID를 통해 해당 회원의 친구를 모두 삭제한다")
    void deleteByUser() {
        // when
        friendRepository.deleteFriendByUser(user);

        // then
        assertThat(friendRepository.findAll().isEmpty()).isTrue();
    }
}
