package umc.stockoneqback.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.global.base.GlobalErrorCode;
import umc.stockoneqback.product.dto.request.EditProductRequest;
import umc.stockoneqback.product.dto.response.*;
import umc.stockoneqback.product.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductApiController {
    private final ProductService productService;

    @GetMapping("")
    public BaseResponse<GetRequiredInfoResponse> getRequiredInfo(@ExtractPayload Long userId) {
        return new BaseResponse<>(productService.getRequiredInfo(userId));
    }

    @PostMapping("/add")
    public BaseResponse<GlobalErrorCode> saveProduct(@ExtractPayload Long userId,
                                                     @RequestParam(value = "store") Long storeId,
                                                        @RequestParam(value = "condition") String storeConditionValue,
                                                        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                        @RequestPart(value = "editProductRequest") EditProductRequest editProductRequest) {
        productService.saveProduct(userId, storeId, storeConditionValue, editProductRequest.toProduct(), multipartFile);
        return new BaseResponse<>(GlobalErrorCode.CREATED);
    }

    @GetMapping("/{productId}")
    public BaseResponse<LoadProductResponse> loadProduct(@ExtractPayload Long userId,
                                                         @PathVariable(value = "productId") Long productId) throws IOException {
        return new BaseResponse<>(productService.loadProduct(userId, productId));
    }

    @GetMapping("/search")
    public BaseResponse<List<SearchProductResponse>> searchProduct(@ExtractPayload Long userId,
                                                                   @RequestParam(value = "store") Long storeId,
                                                                   @RequestParam(value = "condition") String storeConditionValue,
                                                                   @RequestParam(value = "name") String productName) throws IOException {
        return new BaseResponse<>(productService.searchProduct(userId, storeId, storeConditionValue, productName));
    }

    @PatchMapping("/edit/{productId}")
    public BaseResponse<GlobalErrorCode> editProduct(@ExtractPayload Long userId,
                                                     @PathVariable(value = "productId") Long productId,
                                                        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                        @RequestPart(value = "editProductRequest") EditProductRequest editProductRequest) {
        productService.editProduct(userId, productId, editProductRequest.toProduct(), multipartFile);
        return new BaseResponse<>(GlobalErrorCode.SUCCESS);
    }

    @DeleteMapping("/delete/{productId}")
    public BaseResponse<GlobalErrorCode> deleteProduct(@ExtractPayload Long userId,
                                                       @PathVariable(value = "productId") Long productId) {
        productService.deleteProduct(userId, productId);
        return new BaseResponse<>(GlobalErrorCode.SUCCESS);
    }

    @GetMapping("/count")
    public BaseResponse<List<GetTotalProductResponse>> getTotalProduct(@ExtractPayload Long userId,
                                                                       @RequestParam(value = "store") Long storeId,
                                                                       @RequestParam(value = "condition") String storeConditionValue) {
        return new BaseResponse<>(productService.getTotalProduct(userId, storeId, storeConditionValue));
    }

    @GetMapping("/all")
    public BaseResponse<List<SearchProductResponse>> getListOfAllProduct(@ExtractPayload Long userId,
                                                                         @RequestParam(value = "store") Long storeId,
                                                                          @RequestParam(value = "condition") String storeConditionValue,
                                                                          @RequestParam(value = "last", required = false) Long productId,
                                                                          @RequestParam(value = "sort") String sortBy) throws IOException {
        return new BaseResponse<>(productService.getListOfAllProduct(userId, storeId, storeConditionValue, productId, sortBy));
    }

    @GetMapping("/pass")
    public BaseResponse<List<SearchProductResponse>> getListOfPassProduct(@ExtractPayload Long userId,
                                                                          @RequestParam(value = "store") Long storeId,
                                                                         @RequestParam(value = "condition") String storeConditionValue,
                                                                         @RequestParam(value = "last", required = false) Long productId,
                                                                         @RequestParam(value = "sort") String sortBy) throws IOException {
        return new BaseResponse<>(productService.getListOfPassProduct(userId, storeId, storeConditionValue, productId, sortBy));
    }

    @GetMapping("/close")
    public BaseResponse<List<SearchProductResponse>> getListOfCloseProduct(@ExtractPayload Long userId,
                                                                           @RequestParam(value = "store") Long storeId,
                                                                         @RequestParam(value = "condition") String storeConditionValue,
                                                                         @RequestParam(value = "last", required = false) Long productId,
                                                                         @RequestParam(value = "sort") String sortBy) throws IOException {
        return new BaseResponse<>(productService.getListOfCloseProduct(userId, storeId, storeConditionValue, productId, sortBy));
    }

    @GetMapping("/lack")
    public BaseResponse<List<SearchProductResponse>> getListOfLackProduct(@ExtractPayload Long userId,
                                                                          @RequestParam(value = "store") Long storeId,
                                                                         @RequestParam(value = "condition") String storeConditionValue,
                                                                         @RequestParam(value = "last", required = false) Long productId,
                                                                         @RequestParam(value = "sort") String sortBy) throws IOException {
        return new BaseResponse<>(productService.getListOfLackProduct(userId, storeId, storeConditionValue, productId, sortBy));
    }

    @GetMapping("/pass/routine")
    public BaseResponse<List<GetListOfPassProductByOnlineUsersResponse>> getListOfPassProductByOnlineUsers(@ExtractPayload Long userId) {
        return new BaseResponse<>(productService.getListOfPassProductByOnlineUsers());
    }
}
