package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseEntity;
import keapoint.onlog.post.dto.post.PutModifyPostReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToMany
    @JoinTable(
            name = "Post_HashTag_Table",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> hashtagList = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // 게시글 댓글

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id")
    private Blog writer; // 작성자

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setWriter(Blog blog) {
        this.writer = blog;
    }

    /**
     * 게시글 방문
     */
    public void hit() {
        this.postHits += 1; // 방문 횟수 1 증가
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
        this.setWriter(blog);
    }

    /**
     * 게시글 제거
     *
     * @param blog 사용자 blog
     */
    public void removePost(Blog blog) {
        blog.getPostList().remove(this);
        this.setWriter(null);
    }

    public void modifyPost(PutModifyPostReqDto dto, Category category, List<Hashtag> hashtagList) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.summary = dto.getSummary();
        this.thumbnailLink = dto.getThumbnailLink();
        this.isPublic = dto.getIsPublic();
        this.category = category;
        this.hashtagList = hashtagList;
    }
}
