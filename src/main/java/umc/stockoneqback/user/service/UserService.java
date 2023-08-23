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
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.reply.service.ReplyService;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.PartTimer;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.CompanyService;
import umc.stockoneqback.role.service.PartTimerService;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.share.service.ShareService;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
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
    public Long saveManager(User user, Long storeId) {
        validateDuplicate(user.getLoginId(), user.getEmail());

        Store store = storeService.findById(storeId);
        userRepository.save(user);
        store.updateStoreManager(user);

        return user.getId();
    }

    @Transactional
    public Long savePartTimer(User user, String storeName, String storeCode) {
        validateDuplicate(user.getLoginId(), user.getEmail());
        Store store = storeService.findByName(storeName);
        validateStoreCode(storeCode, store.getCode());

        userRepository.save(user);
        PartTimer partTimer = store.updateStorePartTimers(user);
        partTimerService.savePartTimer(partTimer);

        return user.getId();
    }

    private void validateStoreCode(String userCode, String savedCode) {
        if (!userCode.equals(savedCode)) {
            throw BaseException.type(UserErrorCode.INVALID_STORE_CODE);
        }
    }

    @Transactional
    public Long saveSupervisor(User user, String companyName, String companyCode) {
        validateDuplicate(user.getLoginId(), user.getEmail());
        Company company = companyService.findByName(companyName);
        validateCompanyCode(companyCode, company.getCode());

        userRepository.save(user);
        company.addEmployees(user);

        return user.getId();
    }

    private void validateCompanyCode(String userCode, String savedCode) {
        if (!userCode.equals(savedCode)) {
            throw BaseException.type(UserErrorCode.INVALID_COMPANY_CODE);
        }
    }

    private void validateDuplicate(String loginId, Email email) {
        validateDuplicateLoginId(loginId);
        validateDuplicateEmail(email);
    }

    private void validateDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginIdAndStatus(loginId, Status.NORMAL)) {
            throw BaseException.type(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    private void validateDuplicateEmail(Email email) {
        if (userRepository.existsByEmailAndStatus(email, Status.NORMAL)) {
            throw BaseException.type(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

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

    @Transactional
    public void deleteExpiredUser() {
        LocalDate overYear = LocalDate.now().minusYears(1);
        userRepository.deleteModifiedOverYearAndExpireUser(overYear);
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
