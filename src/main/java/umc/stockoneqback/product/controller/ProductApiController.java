package umc.stockoneqback.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.global.exception.GlobalErrorCode;
import umc.stockoneqback.product.controller.dto.request.ProductRequest;
import umc.stockoneqback.product.service.ProductService;
import umc.stockoneqback.product.service.response.GetRequiredInfoResponse;
import umc.stockoneqback.product.service.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.response.LoadProductResponse;
import umc.stockoneqback.product.service.response.SearchProductResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductApiController {
    private final ProductService productService;

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @GetMapping("")
    public BaseResponse<GetRequiredInfoResponse> getRequiredInfo(@ExtractPayload Long userId) {
        return new BaseResponse<>(productService.getRequiredInfo(userId));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @PostMapping("/add")
    public BaseResponse<GlobalErrorCode> saveProduct(@ExtractPayload Long userId,
                                                     @RequestParam(value = "store") Long storeId,
                                                     @RequestParam(value = "condition") String storeConditionValue,
                                                     @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                     @RequestPart(value = "editProductRequest") ProductRequest productRequest) {
        productService.saveProduct(userId, storeId, storeConditionValue, productRequest.toProduct(), multipartFile);
        return new BaseResponse<>(GlobalErrorCode.CREATED);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @GetMapping("/{productId}")
    public BaseResponse<LoadProductResponse> loadProduct(@ExtractPayload Long userId,
                                                         @PathVariable(value = "productId") Long productId) throws IOException {
        return new BaseResponse<>(productService.loadProduct(userId, productId));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @GetMapping("/search")
    public BaseResponse<List<SearchProductResponse>> searchProduct(@ExtractPayload Long userId,
                                                                   @RequestParam(value = "store") Long storeId,
                                                                   @RequestParam(value = "condition") String storeConditionValue,
                                                                   @RequestParam(value = "name") String productName) throws IOException {
        return new BaseResponse<>(productService.searchProduct(userId, storeId, storeConditionValue, productName));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @PatchMapping("/edit/{productId}")
    public BaseResponse<GlobalErrorCode> editProduct(@ExtractPayload Long userId,
                                                     @PathVariable(value = "productId") Long productId,
                                                     @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                     @RequestPart(value = "editProductRequest") ProductRequest productRequest) {
        productService.editProduct(userId, productId, productRequest.toProduct(), multipartFile);
        return new BaseResponse<>(GlobalErrorCode.SUCCESS);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @DeleteMapping("/delete/{productId}")
    public BaseResponse<GlobalErrorCode> deleteProduct(@ExtractPayload Long userId,
                                                       @PathVariable(value = "productId") Long productId) {
        productService.deleteProduct(userId, productId);
        return new BaseResponse<>(GlobalErrorCode.SUCCESS);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @GetMapping("/count")
    public BaseResponse<List<GetTotalProductResponse>> getTotalProduct(@ExtractPayload Long userId,
                                                                       @RequestParam(value = "store") Long storeId,
                                                                       @RequestParam(value = "condition") String storeConditionValue) {
        return new BaseResponse<>(productService.getTotalProduct(userId, storeId, storeConditionValue));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @GetMapping("/page")
    public BaseResponse<List<SearchProductResponse>> getListOfSearchConditionProduct(@ExtractPayload Long userId,
                                                                                     @RequestParam(value = "store") Long storeId,
                                                                                     @RequestParam(value = "condition") String storeConditionValue,
                                                                                     @RequestParam(value = "search") String searchConditionValue,
                                                                                     @RequestParam(value = "last", defaultValue = "-1", required = false) Long productId,
                                                                                     @RequestParam(value = "sort") String sortConditionValue) throws IOException {
        return new BaseResponse<>(productService.getListOfSearchProduct(userId, storeId, storeConditionValue, searchConditionValue, productId, sortConditionValue));
    }
}
