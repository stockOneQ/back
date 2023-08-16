package umc.stockoneqback.share.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.business.exception.BusinessErrorCode;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.share.controller.dto.ShareRequest;
import umc.stockoneqback.share.controller.dto.ShareResponse;
import umc.stockoneqback.share.domain.Category;
import umc.stockoneqback.share.domain.Share;
import umc.stockoneqback.share.exception.ShareErrorCode;
import umc.stockoneqback.share.repository.ShareRepository;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

import java.util.List;

import static umc.stockoneqback.share.controller.dto.ShareResponse.toResponse;
import static umc.stockoneqback.share.domain.Category.findCategoryByValue;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShareService {
    private final ShareRepository shareRepository;
    private final FileService fileService;
    private final BusinessRepository businessRepository;
    private final UserFindService userFindService;

    @Transactional
    public void create(Long userId, Long businessId, String categoryValue, ShareRequest request, MultipartFile file) {
        validateSupervisor(userId);
        Category category = findCategoryByValue(categoryValue);
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> BaseException.type(BusinessErrorCode.BUSINESS_NOT_FOUND));

        String fileUrl = null;

        if (file != null || ((file != null) && !(file.isEmpty()))){
            fileUrl = fileService.uploadShareFiles(file);
        }

        Share share = Share.createShare(request.title(), fileUrl, request.content(), category, business);
        shareRepository.save(share);
    }

    @Transactional
    public void update(Long userId, Long shareId, ShareRequest request, MultipartFile file){
        Share share = findShareById(shareId);
        validateWriter(userId, share);

        String fileUrl = null;

        if (file != null || ((file != null) && !(file.isEmpty()))){
            fileUrl = fileService.uploadShareFiles(file);
        }

        share.update(request.title(), fileUrl, request.content());
    }

    @Transactional
    public ShareResponse detail(Long userId, Long shareId) {
        Share share = findShareById(shareId);
        boolean isWriter = validateViewer(userId, share);

        return toResponse(share, isWriter);
    }

    @Transactional
    public void delete(Long userId, List<Long> selectedShareId) {
        for (Long shareId : selectedShareId) {
            Share share = findShareById(shareId);
            validateWriter(userId, share);
            shareRepository.deleteById(shareId);
        }
    }

    public Share findShareById(Long shareId) {
        return shareRepository.findById(shareId)
                .orElseThrow(() -> BaseException.type(ShareErrorCode.SHARE_NOT_FOUND));
    }

    private void validateSupervisor(Long userId) {
        User user = userFindService.findById(userId);
        if (user.getRole() != Role.SUPERVISOR) {
            throw BaseException.type(UserErrorCode.USER_NOT_ALLOWED);
        }
    }

    private void validateWriter(Long userId, Share share) {
        Long writerId = share.getBusiness().getSupervisor().getId();
        if (writerId != userId) {
            throw BaseException.type(ShareErrorCode.NOT_A_WRITER);
        }
    }

    private boolean validateViewer(Long userId, Share share) {
        User user = userFindService.findById(userId);
        boolean isSupervisor = share.getBusiness().getSupervisor().equals(user);
        boolean isManager = share.getBusiness().getManager().equals(user);
        boolean isPartTimer = share.getBusiness().getManager().getManagerStore().getPartTimers().getPartTimers().contains(user);

        if ((isSupervisor || isManager || isPartTimer) == false) {
            throw BaseException.type(ShareErrorCode.USER_NOT_ALLOWED);
        }

        if (isSupervisor) return true;
        else return false;
    }

    @Transactional
    public void deleteShareByBusiness(Business business) {
        shareRepository.deleteByBusiness(business);
    }
}
