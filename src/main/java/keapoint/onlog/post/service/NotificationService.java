package keapoint.onlog.post.service;

import keapoint.onlog.post.dto.blog.follow.BlogFollowDto;
import keapoint.onlog.post.dto.post.PostDto;
import keapoint.onlog.post.entity.Notification;
import keapoint.onlog.post.repository.NotificationMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate; // 수정된 부분
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMongoRepository notificationMongoRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate; // 수정된 부분

    public Flux<BlogFollowDto> getNewFollows() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("operationType").is("insert").and("fullDocument.type").is("follow"))
        );
        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(aggregation)
                .build();

        return reactiveMongoTemplate.changeStream("notification", options, Notification.class) // 수정된 부분
                .map(ChangeStreamEvent::getBody)
                .map(follow -> new BlogFollowDto(UUID.fromString(((Notification.FollowDetail) follow.getAfter()).getBlog_id()), UUID.fromString(((Notification.FollowDetail) follow.getAfter()).getFollow_id()), ((Notification.FollowDetail) follow.getAfter()).is_following()));
    }

    public Flux<PostDto> getNewPosts() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("operationType").is("insert").and("fullDocument.type").is("post"))
        );
        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(aggregation)
                .build();

        return reactiveMongoTemplate.changeStream("notification", options, Notification.class) // 수정된 부분
                .map(ChangeStreamEvent::getBody)
                .map(post -> {
                    Notification.PostDetail postDetail = (Notification.PostDetail) post.getAfter();
                    PostDto postDto = new PostDto();
                    postDto.setPostId(UUID.fromString(postDetail.getPost_id()));
                    postDto.setPostHits(postDetail.getPost_hits());
                    postDto.setTitle(postDetail.getPost_title());
                    postDto.setContent(postDetail.getPost_content());
                    postDto.setSummary(postDetail.getPost_summary());
                    postDto.setThumbnailLink(postDetail.getPost_thumbnail_link());
                    postDto.setLikesCount(postDetail.getPost_likes_count());
                    postDto.setIsPublic(postDetail.isPost_public());
                    // postDto의 나머지 필드들은 적절한 값을 설정하거나 null을 유지합니다.
                    return postDto;
                });
    }
}
