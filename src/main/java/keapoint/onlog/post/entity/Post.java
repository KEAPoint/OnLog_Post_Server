package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "post")
public class Post extends BaseEntity {

    @Id
    @Column(name = "post_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID postId; // 게시글 식별자

    @Column(name = "post_hits", nullable = false)
    private Long postHits; // 게시글 방문 횟수

    @Column(name = "post_title", nullable = false)
    private String title; // 게시글 제목

    @Column(name = "post_content", nullable = false, columnDefinition = "TEXT")
    private String content; // 게시글 내용

    @Column(name = "post_summary", nullable = false, columnDefinition = "TEXT")
    private String summary; // 게시글 3줄 요약

    @Column(name = "post_thumbnail_link", nullable = false)
    private String thumbnailLink; // 게시글 thumbnail 사진 위치

    @Column(name = "post_public", nullable = false)
    private Boolean isPublic; // 게시글 공개 여부

    @Column(name = "post_modified", nullable = false)
    private Boolean modified; // 게시글 수정 여부

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category; // 게시글 카테고리

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHashtag> postHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // 게시글 댓글

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id")
    private Blog blog; // 사용자 블로그

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    /**
     * 카테고리 추가
     *
     * @param category
     */
    public void addCategory(Category category) {
        category.getPosts().add(this);
        this.setCategory(category);
    }

    /**
     * 카테고리 삭제
     *
     * @param category
     */
    public void removeCategory(Category category) {
        category.getPosts().remove(this);
        this.setCategory(null);
    }

    /**
     * 게시글 추가
     *
     * @param blog 사용자 blog
     */
    public void addPost(Blog blog) {
        blog.getPostList().add(this);
        this.setBlog(blog);
    }

    /**
     * 게시글 제거
     *
     * @param blog 사용자 blog
     */
    public void removePost(Blog blog) {
        blog.getPostList().remove(this);
        this.setBlog(null);
    }

}
