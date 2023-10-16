package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.post.PostCreateBlogReqDto;
import keapoint.onlog.post.dto.post.PostCreateBlogResDto;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    @Transactional
    public PostCreateBlogResDto createBlog(PostCreateBlogReqDto data) throws BaseException {
        try {
            // 로깅
            log.info("블로그 생성 요청 정보: " + data.toString());

            // 해당 id를 기반으로 이미 블로그가 있는 경우 예외를 터트린다.
            if (blogRepository.findById(data.getBlogId()).isPresent())
                throw new BaseException(BaseErrorCode.ALREADY_BLOG_EXISTS_EXCEPTION);

            // 예외가 없는 경우 블로그를 생성한다.
            Blog blog = blogRepository.save(data.toEntity());
            log.info("생성된 블로그 정보: " + blog);

            // 생성 결과를 반환한다.
            return new PostCreateBlogResDto(true);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
