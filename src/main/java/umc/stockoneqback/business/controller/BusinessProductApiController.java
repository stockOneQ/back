package umc.stockoneqback.business.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.business.service.BusinessProductService;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business/product")
public class BusinessProductApiController {
    private final BusinessProductService businessProductService;

    @GetMapping("/search")
    public BaseResponse<List<SearchProductOthersResponse>> searchProductOthers(@ExtractPayload Long supervisorId,
                                                                               @RequestParam(value = "manager") Long managerId,
                                                                               @RequestParam(value = "condition") String storeConditionValue,
                                                                               @RequestParam(value = "name") String productName) throws IOException {
        return new BaseResponse<>(businessProductService.searchProductOthers(supervisorId, managerId, storeConditionValue, productName));
    }

    @GetMapping("/count")
    public BaseResponse<List<GetTotalProductResponse>> getTotalProductOthers(@ExtractPayload Long supervisorId,
                                                                             @RequestParam(value = "manager") Long managerId,
                                                                             @RequestParam(value = "condition") String storeConditionValue) throws IOException {
        return new BaseResponse<>(businessProductService.getTotalProductOthers(supervisorId, managerId, storeConditionValue));
    }

    @GetMapping("/page")
    public BaseResponse<List<SearchProductOthersResponse>> getListOfSearchConditionProductOthers(@ExtractPayload Long supervisorId,
                                                                                     @RequestParam(value = "manager") Long managerId,
                                                                                     @RequestParam(value = "condition") String storeConditionValue,
                                                                                     @RequestParam(value = "search") String searchConditionValue,
                                                                                     @RequestParam(value = "last", defaultValue = "-1", required = false) Long productId) throws IOException {
        return new BaseResponse<>(businessProductService.getListOfSearchProductOthers(supervisorId, managerId, storeConditionValue, productId, searchConditionValue));
    }
}
