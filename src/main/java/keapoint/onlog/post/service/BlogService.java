package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.blog.*;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.repository.BlogRepository;
import keapoint.onlog.post.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BlogService {

    private final FollowRepository followRepository;
    private final BlogRepository blogRepository;

    /**
     * 블로그 생성
     *
     * @param data 생성하고자 하는 블로그 정보
     * @return 생성된 블로그 정보
     */
    public BlogDto createBlog(PostCreateBlogReqDto data) throws BaseException {
        try {
            // 로깅
            log.info("블로그 생성 요청 정보: " + data.toString());

            // 해당 id를 기반으로 이미 블로그가 있는 경우 예외를 터트린다.
            if (blogRepository.findById(data.getBlogId()).isPresent())
                throw new BaseException(BaseErrorCode.ALREADY_BLOG_EXISTS_EXCEPTION);

            // 해당 nickname의 블로그가 있는 경우 예외를 터트린다.
            if (blogRepository.findByBlogNickname(data.getBlogNickname()).isPresent())
                throw new BaseException(BaseErrorCode.ALREADY_BLOG_NICKNAME_EXISTS_EXCEPTION);

            // 예외가 없는 경우 블로그를 생성한다.
            Blog blog = blogRepository.save(data.toEntity(data.getBlogId()));
            log.info("생성된 블로그 정보: " + blog);

            // 생성 결과를 반환한다.
            return new BlogDto(blog);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 블로그 수정
     *
     * @param blogId 수정하고자 하는 블로그 식별자
     * @param data   수정하고자 하는 블로그 내용
     * @return 수정된 블로그 정보
     */
    public BlogDto updateBlog(UUID blogId, PutUpdateBlogReqDto data) throws BaseException {
        try {
            // 로깅
            log.info("블로그 수정 요청 정보: " + data.toString());

            // 블로그 조회, 수정 할 블로그 정보가 없다면 예외를 터트린다.
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 블로그 정보 업데이트
            blog.updateUserProfile(data);

            // 수정된 블로그 정보를 반환한다.
            return new BlogDto(blog);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 블로그 탈퇴
     *
     * @param blogId 탈퇴를 원하는 블로그 식별자
     * @return 탈퇴된 블로그 정보
     */
    public BlogDto deleteBlog(UUID blogId) throws BaseException {
        try {
            // 로깅
            log.info("블로그 탈퇴 요청 정보: " + blogId);

            // 블로그 조회, 탈퇴 할 블로그 정보가 없다면 예외를 터트린다.
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 블로그 탈퇴를 진행한다.
            blogRepository.delete(blog);

            // 탈퇴 된 블로그 정보를 반환한다.
            return new BlogDto(blog);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * 블로그 프로필 조회
     *
     * @param blogId 프로필 조회를 하고자 하는 블로그 식별자
     * @return 블로그 프로필 정보
     */
    @Transactional(readOnly = true)
    public BlogProfileDto getProfile(UUID blogId) throws BaseException {
        try {
            // 블로그 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 구독자 수 조회
            int subscriberCount = followRepository.countByMeAndFollowingIsTrue(blog);

            return new BlogProfileDto(blog, subscriberCount);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
