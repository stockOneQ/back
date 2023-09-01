package umc.stockoneqback.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.product.service.ProductFindService;
import umc.stockoneqback.product.service.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.dto.response.SearchProductResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductFindApiController {
    private final ProductFindService productFindService;

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @GetMapping("/search")
    public BaseResponse<List<SearchProductResponse>> searchProduct(@ExtractPayload Long userId,
                                                                   @RequestParam(value = "store") Long storeId,
                                                                   @RequestParam(value = "condition") String storeConditionValue,
                                                                   @RequestParam(value = "name") String productName) throws IOException {
        return new BaseResponse<>(productFindService.searchProduct(userId, storeId, storeConditionValue, productName));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @GetMapping("/count")
    public BaseResponse<List<GetTotalProductResponse>> getTotalProduct(@ExtractPayload Long userId,
                                                                       @RequestParam(value = "store") Long storeId,
                                                                       @RequestParam(value = "condition") String storeConditionValue) {
        return new BaseResponse<>(productFindService.getTotalProduct(userId, storeId, storeConditionValue));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @GetMapping("/page")
    public BaseResponse<List<SearchProductResponse>> getListOfSearchConditionProduct(@ExtractPayload Long userId,
                                                                                     @RequestParam(value = "store") Long storeId,
                                                                                     @RequestParam(value = "condition") String storeConditionValue,
                                                                                     @RequestParam(value = "search") String searchConditionValue,
                                                                                     @RequestParam(value = "last", defaultValue = "-1", required = false) Long productId,
                                                                                     @RequestParam(value = "sort") String sortConditionValue) throws IOException {
        return new BaseResponse<>(productFindService.getListOfSearchProduct(userId, storeId, storeConditionValue, searchConditionValue, productId, sortConditionValue));
    }
}
