package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseEntity;
import keapoint.onlog.post.dto.blog.PutUpdateBlogReqDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blog")
public class Blog extends BaseEntity {

    @Id
    @Column(name = "blog_id", nullable = false)
    private UUID blogId; // 사용자 블로그 id

    @Column(name = "blog_name", nullable = false, length = 36)
    private String blogName; // 사용자 블로그 이름

    @Column(name = "blog_nickname", nullable = false, length = 36, unique = true)
    private String blogNickname; // 사용자 블로그 별명 (닉네임)

    @Column(name = "blog_profile_img", columnDefinition = "TEXT")
    private String blogProfileImg; // 사용자 블로그 프로필

    @Column(name = "blog_intro", length = 70)
    private String blogIntro; // 사용자 블로그 한 줄 소개

    @Column(name = "blog_theme_img", columnDefinition = "TEXT")
    private String blogThemeImg; // 사용자 블로그 테마 이미지

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "blog_id")
    private List<Category> categories = new ArrayList<>(); // 블로그가 소유한 카테고리 리스트

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public void updateUserProfile(PutUpdateBlogReqDto data) {
        this.blogName = data.getBlogName();
        this.blogNickname = data.getBlogNickname();
        this.blogIntro = data.getBlogIntro();
        this.blogProfileImg = data.getBlogProfileImg();
    }

    /**
     * 게시글 추가 (연관관계 편의 메소드)
     *
     * @param post 게시글
     */
    public void addNewPost(Post post) {
        this.postList.add(post); // 현재 블로그의 게시글 목록에 게시글을 추가
        post.setWriter(this); // 게시글의 작성자를 현재 블로그로 설정
    }

    /**
     * 게시글 삭제 (연관관계 편의 메소드)
     *
     * @param post 게시글
     */
    public void removeExistingPost(Post post) {
        this.postList.remove(post); // 현재 블로그의 게시글 목록에서 게시글을 제거
        post.setWriter(null); // 게시글의 작성자 정보를 제거
    }

    @Override
    public String toString() {
        return "Blog{" +
                "blogId=" + blogId +
                ", blogName='" + blogName + '\'' +
                ", blogNickname='" + blogNickname + '\'' +
                ", blogProfileImg='" + blogProfileImg + '\'' +
                ", blogIntro='" + blogIntro + '\'' +
                ", blogThemeImg='" + blogThemeImg + '\'' +
                '}';
    }
}