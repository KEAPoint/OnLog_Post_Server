package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseEntity;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@ToString
@Table(name = "blog")
public class Blog extends BaseEntity {

    @Id
    @Column(name = "blog_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID blogId; // 사용자 블로그 id

    @Column(name = "blog_name", nullable = false, length = 32)
    private String blogName; // 사용자 블로그 이름

    @Column(name = "blog_nickname", nullable = false, length = 32)
    private String blogNickname; // 사용자 블로그 별명

    @Column(name = "blog_profile_img", nullable = false)
    private String blogProfileImg; // 사용자 블로그 프로필

    @Column(name = "blog_intro")
    private String blogIntro; // 사용자 블로그 한 줄 소개

    @Column(name = "blog_theme_img", nullable = false)
    private String blogThemeImg; // 사용자 블로그 테마 이미지

    @OneToMany(mappedBy = "blog", orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

}

