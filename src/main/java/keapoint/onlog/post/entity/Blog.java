package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blog")
public class Blog extends BaseEntity {

    @Id
    @Column(name = "blog_id", nullable = false)
    private UUID blogId; // 사용자 블로그 id

    @Column(name = "blog_name", nullable = false, length = 32)
    private String blogName; // 사용자 블로그 이름

    @Column(name = "blog_nickname", nullable = false, length = 32, unique = true)
    private String blogNickname; // 사용자 블로그 별명

    @Column(name = "blog_profile_img")
    private String blogProfileImg; // 사용자 블로그 프로필

    @Column(name = "blog_intro")
    private String blogIntro; // 사용자 블로그 한 줄 소개

    @Column(name = "blog_theme_img")
    private String blogThemeImg; // 사용자 블로그 테마 이미지

    @OneToMany(mappedBy = "writer", orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "categoryOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> categories; // 블로그가 소유한 카테고리 리스트

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

}

