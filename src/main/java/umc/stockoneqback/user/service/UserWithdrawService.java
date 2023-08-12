package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.board.service.BoardService;
import umc.stockoneqback.board.service.like.BoardLikeService;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.service.BusinessService;
import umc.stockoneqback.comment.service.CommentService;
import umc.stockoneqback.friend.service.FriendService;
import umc.stockoneqback.reply.service.ReplyService;
import umc.stockoneqback.role.service.CompanyService;
import umc.stockoneqback.role.service.PartTimerService;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.share.service.ShareService;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserWithdrawService {
    private final UserFindService userFindService;
    private final PartTimerService partTimerService;
    private final BusinessService businessService;
    private final ShareService shareService;
    private final StoreService storeService;
    private final CompanyService companyService;
    private final FriendService friendService;
    private final BoardService boardService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final BoardLikeService boardLikeService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Transactional
    public void withdrawUser(Long userId) {
        User user = userFindService.findById(userId);
        switch (user.getRole()) {
            case MANAGER -> {
                deleteInfoByManager(user);
            }
            case PART_TIMER -> {
                deleteInfoByPartTimer(user);
            }
            case SUPERVISOR -> {
                deleteInfoBySupervisor(user);
            }
        }
        authService.logout(userId);
        userRepository.expireById(userId);
    }

    private void deleteInfoByManager(User manager) {
        List<Business> businessList = businessService.getBusinessByManager(manager);
        deleteBusinessWithShare(businessList);
        storeService.deleteManager(manager.getManagerStore());
        friendService.deleteFriendByUser(manager);
        replyService.deleteByWriter(manager);
        commentService.deleteByWriter(manager);
        boardService.deleteByWriter(manager);
        boardLikeService.deleteByUser(manager);
    }

    private void deleteInfoByPartTimer(User partTimer) {
        partTimerService.deleteByUser(partTimer);
    }

    private void deleteInfoBySupervisor(User supervisor) {
        List<Business> businessList = businessService.getBusinessBySupervisor(supervisor);
        deleteBusinessWithShare(businessList);
        companyService.deleteSupervisorByUser(supervisor.getCompany(), supervisor);
    }

    private void deleteBusinessWithShare(List<Business> businessList) {
        for (Business business: businessList) {
            shareService.deleteShareByBusiness(business);
            businessService.deleteBusiness(business);
        }
    }
}
