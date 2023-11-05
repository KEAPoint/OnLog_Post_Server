package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseEntity;
import keapoint.onlog.post.dto.post.PostWritePostReqDto;
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

    @Column(name = "post_thumbnail_link", nullable = false, columnDefinition = "TEXT")
    private String thumbnailLink; // 게시글 thumbnail 사진 위치

    @Column(name = "post_public", nullable = false)
    private Boolean isPublic; // 게시글 공개 여부

    @Column(name = "post_modified", nullable = false)
    private Boolean modified; // 게시글 수정 여부

    @Column(name = "post_likes_count", nullable = false)
    private Long likesCount; // 게시글 좋아요 갯수

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category; // 게시글 카테고리

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id")
    private Topic topic; // 게시글 주제

    @ManyToMany
    @JoinTable(
            name = "Post_HashTag_Table",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> hashtagList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>(); // 게시글 댓글

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id")
    private Blog writer; // 작성자

    protected void setWriter(Blog blog) {
        this.writer = blog;
    }

    /**
     * 게시글 방문
     */
    public void hit() {
        this.postHits += 1; // 방문 횟수 1 증가
    }

    /**
     * 게시글 좋아요
     */
    public void postLike() {
        this.likesCount += 1;
    }

    /**
     * 게시글 좋아요 취소
     */
    public void postUnlike() {
        this.likesCount -= 1;
    }

    /**
     * 게시글 작성
     *
     * @param dto         게시글 작성 요청 DTO
     * @param writer      게시글을 작성하는 사용자
     * @param category    게시글의 카테고리
     * @param topic       게시글의 주제
     * @param hashtagList 게시글의 해시태그 리스트
     */
    public Post(PostWritePostReqDto dto, Blog writer, Category category, Topic topic, List<Hashtag> hashtagList) {
        this.postHits = 0L;
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.summary = dto.getSummary();
        this.thumbnailLink = dto.getThumbnailLink();
        this.isPublic = dto.getIsPublic();
        this.modified = false;
        this.likesCount = 0L;
        this.comments = new ArrayList<>();

        // 연관관계 설정
        writer.addNewPost(this);
        assignCategory(category);
        assignTopic(topic);
        hashtagList.forEach(this::addHashtag);
    }

    /**
     * 게시글 수정
     *
     * @param dto         게시글 수정 요청 DTO
     * @param category    수정된 게시글의 카테고리
     * @param topic       수정된 게시글의 주제
     * @param hashtagList 수정된 게시글의 해시태그 리스트
     */
    public void modifyPost(PutModifyPostReqDto dto, Category category, Topic topic, List<Hashtag> hashtagList) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.summary = dto.getSummary();
        this.thumbnailLink = dto.getThumbnailLink();
        this.isPublic = dto.getIsPublic();
        this.category = category;
        this.hashtagList = hashtagList;

        this.modified = true; // 게시글이 수정되었음을 표시

        // 기존 연관관계 제거
        removeCategory();
        removeAssignedTopic();
        this.hashtagList.forEach(this::removeHashtag);

        // 연관관계 설정
        assignCategory(category);
        assignTopic(topic);
        hashtagList.forEach(this::addHashtag);
    }

    /**
     * 게시글 삭제
     */
    public void deletePost() {
        writer.removeExistingPost(this);
        removeCategory();
        removeAssignedTopic();
        this.hashtagList.forEach(this::removeHashtag);
    }

    /**
     * 게시글 카테고리 설정 (연관관계 편의 메소드)
     *
     * @param category 사용자 카테고리
     */
    public void assignCategory(Category category) {
        this.category = category;
        category.getPosts().add(this);
    }

    /**
     * 게시글 카테고리 삭제 (연관관계 편의 메소드)
     */
    public void removeCategory() {
        if (this.category != null) {
            this.category.getPosts().remove(this);
        }
        this.category = null;
    }

    /**
     * 게시글 해시태그 설정 (연관관계 편의 메소드)
     *
     * @param hashtag 해시태그
     */
    public void addHashtag(Hashtag hashtag) {
        this.hashtagList.add(hashtag);
        hashtag.getPostList().add(this);
    }

    /**
     * 게시글 해시태그 삭제 (연관관계 편의 메소드)
     *
     * @param hashtag 해시태그
     */
    public void removeHashtag(Hashtag hashtag) {
        hashtag.getPostList().remove(this);
        this.hashtagList.remove(hashtag);
    }

    /**
     * 게시글 주제 설정 (연관관계 편의 메소드)
     *
     * @param topic 주제
     */
    public void assignTopic(Topic topic) {
        this.topic = topic;
        topic.getPosts().add(this);
    }

    /**
     * 게시글 주제 삭제 (연관관계 편의 메소드)
     */
    public void removeAssignedTopic() {
        if (this.topic != null) {
            this.topic.getPosts().remove(this);
        }
        this.topic = null;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", postHits=" + postHits +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", summary='" + summary + '\'' +
                ", thumbnailLink='" + thumbnailLink + '\'' +
                ", isPublic=" + isPublic +
                ", modified=" + modified +
                ", likesCount=" + likesCount +
                ", category=" + category +
                ", topic=" + topic +
                ", hashtagList=" + hashtagList +
                ", writer=" + writer.getBlogId() +
                '}';
    }
}
