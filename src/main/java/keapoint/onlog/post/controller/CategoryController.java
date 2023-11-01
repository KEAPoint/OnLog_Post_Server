package keapoint.onlog.post.controller;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.category.PostCreateCategoryReqDto;
import keapoint.onlog.post.service.CategoryService;
import keapoint.onlog.post.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import keapoint.onlog.post.dto.category.CategoryUpdateReqDto;
import keapoint.onlog.post.dto.category.CategoryDeleteReqDto;


import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 카테고리 생성 API
     */
    @PostMapping("")
    public BaseResponse<CategoryDto> createCategory(@RequestHeader("Authorization") String token,
                                                    @RequestBody PostCreateCategoryReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(categoryService.createCategory(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    /**
     * 카테고리 수정 API
     */
    @PutMapping
    public BaseResponse<CategoryDto> updateCategory(@RequestHeader("Authorization") String token,
                                                    @RequestBody CategoryUpdateReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(categoryService.updateCategory(dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    /**
     * 카테고리 삭제 API
     */
    @DeleteMapping
    public BaseResponse<CategoryDto> deleteCategory(@RequestHeader("Authorization") String token,
                                                    @RequestBody CategoryDeleteReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(categoryService.deleteCategory(dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }
}