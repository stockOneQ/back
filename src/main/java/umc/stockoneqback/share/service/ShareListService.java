package umc.stockoneqback.share.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.business.exception.BusinessErrorCode;
import umc.stockoneqback.business.infra.query.dto.FilteredBusinessUser;
import umc.stockoneqback.business.infra.query.dto.FindBusinessUser;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.share.domain.Category;
import umc.stockoneqback.share.domain.ShareSearchType;
import umc.stockoneqback.share.exception.ShareErrorCode;
import umc.stockoneqback.share.infra.query.dto.CustomShareListPage;
import umc.stockoneqback.share.infra.query.dto.ShareList;
import umc.stockoneqback.share.repository.ShareRepository;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

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

        return switch (role) {
            case MANAGER -> businessRepository.findBusinessByManager(userId);
            case PART_TIMER -> businessRepository.findBusinessByPartTimer(userId);
            case SUPERVISOR -> businessRepository.findBusinessBySupervisor(userId);
            default -> null;
        };
    }

    @Transactional
    public CustomShareListPage getShareList(Long userId, Long selectedBusinessId, int page, String category,
                                            String searchType, String searchWord) {
        User user = userFindService.findById(userId);
        Role role = classifyUser(user);

        switch (role) {
            case MANAGER -> {
                businessRepository.findByIdAndManager(selectedBusinessId, user)
                        .orElseThrow(() -> BaseException.type(BusinessErrorCode.BUSINESS_NOT_FOUND));
                return getShareListResponse(selectedBusinessId, page, category, searchType, searchWord);
            }
            case PART_TIMER -> {
                validateFilteredBusiness(businessRepository.findBusinessByPartTimer(userId), selectedBusinessId);
                return getShareListResponse(selectedBusinessId, page, category, searchType, searchWord);
            }
            case SUPERVISOR -> {
                businessRepository.findByIdAndSupervisor(selectedBusinessId, user)
                        .orElseThrow(() -> BaseException.type(BusinessErrorCode.BUSINESS_NOT_FOUND));
                return getShareListResponse(selectedBusinessId, page, category, searchType, searchWord);
            }
            default -> {
                return null;
            }
        }
    }

    private CustomShareListPage<ShareList> getShareListResponse(Long businessId, int page, String categoryValue,
                                                                String searchTypeValue, String searchWord) {
        Category category = Category.from(categoryValue);
        ShareSearchType shareSearchType = ShareSearchType.from(searchTypeValue);
        CustomShareListPage<ShareList> shareList = shareRepository.findShareList(businessId, category, shareSearchType, searchWord, page);
        return shareList;
    }

    private void validateFilteredBusiness(FilteredBusinessUser<FindBusinessUser> filteredBusiness, Long selectedBusinessId) {
        boolean flag = false;
        for (int i = 0; i < filteredBusiness.getTotal(); i++) {
            if (filteredBusiness.getBusinessUserList().get(i).getUserBusinessId().equals(selectedBusinessId)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw BaseException.type(ShareErrorCode.NOT_FILTERED_USER);
        }
    }

    private Role classifyUser(User user) {
        if (user.getRole() == null) {
            throw BaseException.type(UserErrorCode.ROLE_NOT_FOUND);
        }
        return user.getRole();
    }
}