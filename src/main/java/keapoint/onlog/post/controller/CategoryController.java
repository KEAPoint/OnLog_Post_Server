package keapoint.onlog.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import keapoint.onlog.post.dto.category.PutCategoryUpdateReqDto;
import keapoint.onlog.post.dto.category.DeleteCategoryReqDto;


import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@Tag(name = "Blog")
@RequiredArgsConstructor
@RequestMapping("/blog/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "카테고리 조회", description = "특정 유저의 카테고리를 조회합니다.")
    @GetMapping("")
    public BaseResponse<List<CategoryDto>> getCategories(@RequestParam("blog_id") UUID blogId) {
        try {
            return BaseResponse.onSuccess(categoryService.getCategories(blogId));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @PostMapping("")
    public BaseResponse<CategoryDto> createCategory(@RequestHeader("Authorization") String token,
                                                    @RequestBody PostCreateCategoryReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return BaseResponse.onCreate(categoryService.createCategory(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "카테고리 수정", description = "카테고리 이름을 수정합니다.")
    @PutMapping("")
    public BaseResponse<CategoryDto> updateCategory(@RequestHeader("Authorization") String token,
                                                    @RequestBody PutCategoryUpdateReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return BaseResponse.onCreate(categoryService.updateCategory(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다.")
    @DeleteMapping("")
    public BaseResponse<CategoryDto> deleteCategory(@RequestHeader("Authorization") String token,
                                                    @RequestBody DeleteCategoryReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            categoryService.deleteCategory(blogId, dto);
            return BaseResponse.onSuccess(null);

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }
}