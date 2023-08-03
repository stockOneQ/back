package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.friend.service.FriendProductService;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend/product")
public class FriendProductApiController {
    private final FriendProductService friendProductService;

    @GetMapping("/search")
    public BaseResponse<List<SearchProductOthersResponse>> searchProductOthers(@ExtractPayload Long userId,
                                                                                  @RequestParam(value = "friend") Long friendId,
                                                                                  @RequestParam(value = "condition") String storeConditionValue,
                                                                                  @RequestParam(value = "name") String productName) throws IOException {
        return new BaseResponse<>(friendProductService.searchProductOthers(userId, friendId, storeConditionValue, productName));
    }

    @GetMapping("/count")
    public BaseResponse<List<GetTotalProductResponse>> getTotalProductOthers(@ExtractPayload Long userId,
                                                                             @RequestParam(value = "friend") Long friendId,
                                                                       @RequestParam(value = "condition") String storeConditionValue) throws IOException {
        return new BaseResponse<>(friendProductService.getTotalProductOthers(userId, friendId, storeConditionValue));
    }

    @GetMapping("/all")
    public BaseResponse<List<SearchProductOthersResponse>> getListOfAllProductOthers(@ExtractPayload Long userId,
                                                                               @RequestParam(value = "friend") Long friendId,
                                                                         @RequestParam(value = "condition") String storeConditionValue,
                                                                         @RequestParam(value = "last", required = false) Long productId) throws IOException {
        return new BaseResponse<>(friendProductService.getListOfCategoryProductOthers(userId, friendId, storeConditionValue, productId, "All"));
    }

    @GetMapping("/pass")
    public BaseResponse<List<SearchProductOthersResponse>> getListOfPassProductOthers(@ExtractPayload Long userId,
                                                                                @RequestParam(value = "friend") Long friendId,
                                                                                @RequestParam(value = "condition") String storeConditionValue,
                                                                                @RequestParam(value = "last", required = false) Long productId) throws IOException {
        return new BaseResponse<>(friendProductService.getListOfCategoryProductOthers(userId, friendId, storeConditionValue, productId, "Pass"));
    }

    @GetMapping("/close")
    public BaseResponse<List<SearchProductOthersResponse>> getListOfCloseProductOthers(@ExtractPayload Long userId,
                                                                                 @RequestParam(value = "friend") Long friendId,
                                                                                 @RequestParam(value = "condition") String storeConditionValue,
                                                                                 @RequestParam(value = "last", required = false) Long productId) throws IOException {
        return new BaseResponse<>(friendProductService.getListOfCategoryProductOthers(userId, friendId, storeConditionValue, productId, "Close"));
    }

    @GetMapping("/lack")
    public BaseResponse<List<SearchProductOthersResponse>> getListOfLackProductOthers(@ExtractPayload Long userId,
                                                                                @RequestParam(value = "friend") Long friendId,
                                                                                @RequestParam(value = "condition") String storeConditionValue,
                                                                                @RequestParam(value = "last", required = false) Long productId) throws IOException {
        return new BaseResponse<>(friendProductService.getListOfCategoryProductOthers(userId, friendId, storeConditionValue, productId, "Lack"));
    }
}
