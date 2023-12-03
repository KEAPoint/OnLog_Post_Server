package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "follow")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", referencedColumnName = "blog_id")
    private Blog me;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follow_id", referencedColumnName = "blog_id")
    private Blog target;

    @Column(name = "is_following", nullable = false)
    private boolean following; // 해당 블로그를 팔로잉 하고 있는지 여부

    /**
     * 팔로우 업데이트 (팔로우 X <-> 팔로우)
     */
    public void updateFollow(Boolean targetValue) throws BaseException {
        if (this.following == targetValue) {
            throw new BaseException(BaseErrorCode.EXPECTED_FOLLOWING_STATE_EXCEPTION);
        } else {
            this.following = targetValue;
        }
    }

    @Override
    public String toString() {
        return "Follow{" +
                "id=" + id +
                ", me=" + me.getBlogId() +
                ", target=" + target.getBlogId() +
                ", isFollowing=" + following +
                '}';
    }
}