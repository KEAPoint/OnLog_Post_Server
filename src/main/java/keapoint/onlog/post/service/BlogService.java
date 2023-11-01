package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.blog.*;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Follow;
import keapoint.onlog.post.repository.BlogRepository;
import keapoint.onlog.post.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    private final FollowRepository followRepository;

    @Transactional
    public BlogDto createBlog(UUID blogId, PostCreateBlogReqDto data) throws BaseException {
        try {
            // 로깅
            log.info("블로그 생성 요청 정보: " + data.toString());

            // 해당 id를 기반으로 이미 블로그가 있는 경우 예외를 터트린다.
            if (blogRepository.findById(blogId).isPresent())
                throw new BaseException(BaseErrorCode.ALREADY_BLOG_EXISTS_EXCEPTION);

            // 해당 nickname의 블로그가 있는 경우 예외를 터트린다.
            if (blogRepository.findByBlogNickname(data.getBlogNickname()).isPresent())
                throw new BaseException(BaseErrorCode.ALREADY_BLOG_NICKNAME_EXISTS_EXCEPTION);

            // 예외가 없는 경우 블로그를 생성한다.
            Blog blog = blogRepository.save(data.toEntity(blogId));
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

    @Transactional
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

    @Transactional
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

    @Transactional(readOnly = true)
    public BlogDto getProfile(UUID blogId) throws BaseException {
        try {
            // 블로그 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

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
     * 블로그 팔로우 / 언팔로우
     *
     * @param blogId       내 블로그 식별자
     * @param targetBlogId 팔로우/팔로우 취소 할 블로그 식별자
     * @param targetValue  팔로우 할지 말지 여부
     * @return 성공 여부
     */
    @Transactional
    public Boolean toggleFollow(UUID blogId, UUID targetBlogId, Boolean targetValue) throws BaseException {
        try {
            // 내 블로그 조회
            Blog me = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 팔로우 할 블로그 조회
            Blog target = blogRepository.findById(targetBlogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 팔로우 정보 조회
            Follow follow = followRepository.findByMeAndTarget(me, target)
                    .orElseGet(() -> {
                        Follow newFollow = Follow.builder()
                                .me(me)
                                .target(target)
                                .isFollowing(false) // 기존에 팔로우 한 기록이 없으면 팔로우X 상태
                                .build();

                        return followRepository.save(newFollow); // 새로운 팔로우 정보 생성 및 저장
                    });

            // 팔로우 정보 업데이트
            follow.updateFollow(targetValue);

            // 결과 return
            return true;

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
