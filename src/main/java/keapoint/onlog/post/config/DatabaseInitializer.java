package keapoint.onlog.post.config;

import keapoint.onlog.post.entity.Topic;
import keapoint.onlog.post.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final TopicRepository topicRepository;

    @Override
    public void run(String... args) {
        Topic topic1 = new Topic("lifestyle");
        Topic topic2 = new Topic("travel");
        Topic topic3 = new Topic("foodie");
        Topic topic4 = new Topic("entertainment");
        Topic topic5 = new Topic("tech");
        Topic topic6 = new Topic("sports");
        Topic topic7 = new Topic("news");

        // 생성된 토픽 객체 저장
        List<Topic> topics = Arrays.asList(topic1, topic2, topic3, topic4, topic5, topic6, topic7);

        topicRepository.saveAll(topics);
    }
}
