package umc.stockoneqback.share.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.business.exception.BusinessErrorCode;
import umc.stockoneqback.business.infra.query.dto.FilteredBusinessUser;
import umc.stockoneqback.business.infra.query.dto.FindBusinessUser;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.share.domain.Category;
import umc.stockoneqback.share.domain.SearchType;
import umc.stockoneqback.share.exception.ShareErrorCode;
import umc.stockoneqback.share.infra.query.dto.CustomShareListPage;
import umc.stockoneqback.share.infra.query.dto.ShareList;
import umc.stockoneqback.share.repository.ShareRepository;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShareListService {
    private final UserFindService userFindService;
    private final ShareRepository shareRepository;
    private final BusinessRepository businessRepository;

    @Transactional
    public FilteredBusinessUser userSelectBox(Long userId) {
        User user = userFindService.findById(userId);
        Role role = classifyUser(user);

        switch (role) {
            case MANAGER:
                return businessRepository.findBusinessByManager(userId);
            case PART_TIMER:
                return businessRepository.findBusinessByPartTimer(userId);
            case SUPERVISOR:
                return businessRepository.findBusinessBySupervisor(userId);
            default:
                return null;
        }
    }

    @Transactional
    public CustomShareListPage getShareList(Long userId, Long selectedUserId,
                                          int page, String category,
                                          String searchType, String searchWord) throws IOException {
        User user = userFindService.findById(userId);
        Role role = classifyUser(user);

        switch (role) {
            case MANAGER -> {
                validateFilteredBusinessUser(businessRepository.findBusinessByManager(userId), selectedUserId);
                return shareListForManager(userId, selectedUserId, page, category, searchType, searchWord);
            }
            case PART_TIMER -> {
                validateFilteredBusinessUser(businessRepository.findBusinessByPartTimer(userId), selectedUserId);
                return shareListForPartTimer(userId, selectedUserId, page, category, searchType, searchWord);
            }
            case SUPERVISOR -> {
                validateFilteredBusinessUser(businessRepository.findBusinessBySupervisor(userId), selectedUserId);
                return shareListForSupervisor(userId, selectedUserId, page, category, searchType, searchWord);
            }
            default -> {
                return null;
            }
        }
    }

    private CustomShareListPage<ShareList> shareListForManager(Long managerId, Long supervisorId,
                                                               int page, String categoryValue,
                                                               String searchTypeValue, String searchWord) {
        return getManagerOrSupervisorShareList(managerId, supervisorId, page, categoryValue, searchTypeValue, searchWord);
    }

    private CustomShareListPage<ShareList> shareListForPartTimer(Long partTimerId, Long supervisorId,
                                                                 int page, String categoryValue,
                                                                 String searchTypeValue, String searchWord) {
        Long businessId = businessRepository.findBusinessIdByPartTimerIdAndSupervisorId(partTimerId, supervisorId);
        Category category = Category.findCategoryByValue(categoryValue);
        SearchType searchType = SearchType.findShareSearchTypeByValue(searchTypeValue);
        CustomShareListPage<ShareList> shareList = shareRepository.findShareList(businessId, category, searchType, searchWord, page);
        return shareList;
    }

    private CustomShareListPage<ShareList> shareListForSupervisor(Long supervisorId, Long managerId,
                                                                  int page, String categoryValue,
                                                                  String searchTypeValue, String searchWord) {
        return getManagerOrSupervisorShareList(managerId, supervisorId, page, categoryValue, searchTypeValue, searchWord);
    }

    private CustomShareListPage<ShareList> getManagerOrSupervisorShareList(Long managerId, Long supervisorId,
                                                                           int page, String categoryValue,
                                                                           String searchTypeValue, String searchWord) {
        Business business = businessRepository.findByManagerIdAndSupervisorId(managerId, supervisorId)
                .orElseThrow(() -> BaseException.type(BusinessErrorCode.BUSINESS_NOT_FOUND));
        Long businessId = business.getId();
        Category category = Category.findCategoryByValue(categoryValue);
        SearchType searchType = SearchType.findShareSearchTypeByValue(searchTypeValue);
        CustomShareListPage<ShareList> shareList = shareRepository.findShareList(businessId, category, searchType, searchWord, page);
        return shareList;
    }

    private void validateFilteredBusinessUser(FilteredBusinessUser<FindBusinessUser> filteredBusinessUser, Long selectedUserId) {
        boolean flag = false;
        for (int i = 0; i < filteredBusinessUser.getTotal(); i++) {
            if (filteredBusinessUser.getFilterBusinessUserList().get(i).getId() == selectedUserId) {
                flag = true;
                break;
            }
        }
        if(!flag) throw BaseException.type(ShareErrorCode.NOT_FILTERED_USER);
    }

    private Role classifyUser(User user) {
        if (user.getRole() == null) {
            throw BaseException.type(UserErrorCode.ROLE_NOT_FOUND);
        }
        return user.getRole();
    }
}