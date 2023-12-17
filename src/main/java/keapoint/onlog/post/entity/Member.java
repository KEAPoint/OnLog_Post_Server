package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.AccountType;
import keapoint.onlog.post.base.BaseEntity;
import keapoint.onlog.post.base.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_idx", nullable = false)
    private UUID memberIdx;

    @Column(name = "email", length = 40, nullable = false, unique = true)
    private String email;

    private String password;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "agree_personal_info", nullable = false)
    private boolean agreePersonalInfo;

    @Column(name = "agree_promotion", nullable = false)
    private boolean agreePromotion;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(length = 20, name = "name")
    private String userName;

    @OneToMany(mappedBy = "member")
    private List<Blog> blogList;

    @Builder
    public Member(String email, String password, String phoneNumber, boolean agreePersonalInfo, boolean agreePromotion, String refreshToken, Role role, AccountType accountType, String userName) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.agreePersonalInfo = agreePersonalInfo;
        this.agreePromotion = agreePromotion;
        this.refreshToken = refreshToken;
        this.role = role;
        this.accountType = accountType;
        this.userName = userName;
    }

    /**
     * 사용자의 refresh token 갱신
     *
     * @param refreshToken 리프레시 토큰
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 사용자의 refresh token 파기
     */
    public void invalidateRefreshToken() {
        this.refreshToken = null;
    }

    /**
     * 사용자의 mail 수정
     */
    public void updateEmail(String email) {
        this.email = email;
    }
}
