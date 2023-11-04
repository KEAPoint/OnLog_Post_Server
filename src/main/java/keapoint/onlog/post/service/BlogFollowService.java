package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.blog.follow.BlogFollowDto;
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
@Transactional
@RequiredArgsConstructor
public class BlogFollowService {

    private final BlogRepository blogRepository;
    private final FollowRepository followRepository;

    /**
     * 블로그 팔로우 / 언팔로우
     *
     * @param blogId       내 블로그 식별자
     * @param targetBlogId 팔로우/팔로우 취소 할 블로그 식별자
     * @param targetValue  팔로우 할지 말지 여부
     * @return 성공 여부
     */
    public BlogFollowDto toggleFollow(UUID blogId, UUID targetBlogId, Boolean targetValue) throws BaseException {
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
            return new BlogFollowDto(follow);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
