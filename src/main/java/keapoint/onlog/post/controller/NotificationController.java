// NotificationController.java
package keapoint.onlog.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.blog.follow.BlogFollowDto;
import keapoint.onlog.post.dto.post.PostDto;
import keapoint.onlog.post.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@Tag(name = "Notification")
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "새 팔로우 알림", description = "새로운 팔로우 알림을 가져옵니다.")
    @GetMapping("/newFollows")
    public Flux<BlogFollowDto> getNewFollows() { 
        return notificationService.getNewFollows();
    }

    @Operation(summary = "새 게시물 알림", description = "새로운 게시물 알림을 가져옵니다.")
    @GetMapping("/newPosts")
    public Flux<PostDto> getNewPosts() { 
        return notificationService.getNewPosts();
    }
}
