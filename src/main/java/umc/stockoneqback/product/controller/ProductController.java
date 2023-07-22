package umc.stockoneqback.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.global.base.BaseResponseStatus;
import umc.stockoneqback.product.dto.request.EditProductRequest;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.LoadProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductResponse;
import umc.stockoneqback.product.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/add")
    public BaseResponse<BaseResponseStatus> saveProduct(@RequestParam(value = "store") Long storeId,
                                                        @RequestParam(value = "condition") String storeConditionValue,
                                                        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                        @RequestPart(value = "editProductRequest") EditProductRequest editProductRequest) {
        productService.saveProduct(storeId, storeConditionValue, editProductRequest.toProduct(), multipartFile);
        return new BaseResponse<>(BaseResponseStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public BaseResponse<LoadProductResponse> loadProduct(@PathVariable(value = "productId") Long productId) throws IOException {
        return new BaseResponse<>(productService.loadProduct(productId));
    }

    @GetMapping("")
    public BaseResponse<List<SearchProductResponse>> searchProduct(@RequestParam(value = "store") Long storeId,
                                                                   @RequestParam(value = "condition") String storeConditionValue,
                                                                   @RequestParam(value = "name") String productName) throws IOException {
        return new BaseResponse<>(productService.searchProduct(storeId, storeConditionValue, productName));
    }

    @PatchMapping("/edit/{productId}")
    public BaseResponse<BaseResponseStatus> editProduct(@PathVariable(value = "productId") Long productId,
                                                        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                        @RequestPart(value = "editProductRequest") EditProductRequest editProductRequest) {
        productService.editProduct(productId, editProductRequest.toProduct(), multipartFile);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @DeleteMapping("/delete/{productId}")
    public BaseResponse<BaseResponseStatus> deleteProduct(@PathVariable(value = "productId") Long productId) {
        productService.deleteProduct(productId);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @GetMapping("/count")
    public BaseResponse<List<GetTotalProductResponse>> getTotalProduct(@RequestParam(value = "store") Long storeId,
                                                                       @RequestParam(value = "condition") String storeConditionValue) {
        return new BaseResponse<>(productService.getTotalProduct(storeId, storeConditionValue));
    }

    @GetMapping("/all")
    public BaseResponse<List<SearchProductResponse>> getListOfAllProduct(@RequestParam(value = "store") Long storeId,
                                                                          @RequestParam(value = "condition") String storeConditionValue,
                                                                          @RequestParam(value = "last", required = false) Long productId,
                                                                          @RequestParam(value = "sort") String sortBy) throws IOException {
        return new BaseResponse<>(productService.getListOfAllProduct(storeId, storeConditionValue, productId, sortBy));
    }

    @GetMapping("/pass")
    public BaseResponse<List<SearchProductResponse>> getListOfPassProduct(@RequestParam(value = "store") Long storeId,
                                                                         @RequestParam(value = "condition") String storeConditionValue,
                                                                         @RequestParam(value = "last", required = false) Long productId,
                                                                         @RequestParam(value = "sort") String sortBy) throws IOException {
        return new BaseResponse<>(productService.getListOfPassProduct(storeId, storeConditionValue, productId, sortBy));
    }

    @GetMapping("/close")
    public BaseResponse<List<SearchProductResponse>> getListOfCloseProduct(@RequestParam(value = "store") Long storeId,
                                                                         @RequestParam(value = "condition") String storeConditionValue,
                                                                         @RequestParam(value = "last", required = false) Long productId,
                                                                         @RequestParam(value = "sort") String sortBy) throws IOException {
        return new BaseResponse<>(productService.getListOfCloseProduct(storeId, storeConditionValue, productId, sortBy));
    }

    @GetMapping("/lack")
    public BaseResponse<List<SearchProductResponse>> getListOfLackProduct(@RequestParam(value = "store") Long storeId,
                                                                         @RequestParam(value = "condition") String storeConditionValue,
                                                                         @RequestParam(value = "last", required = false) Long productId,
                                                                         @RequestParam(value = "sort") String sortBy) throws IOException {
        return new BaseResponse<>(productService.getListOfLackProduct(storeId, storeConditionValue, productId, sortBy));
    }
}
