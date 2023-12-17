package keapoint.onlog.post.service;

import keapoint.onlog.post.base.AccountType;
import keapoint.onlog.post.dto.auth.PostLoginRes;
import keapoint.onlog.post.dto.auth.SocialAccountUserInfo;
import keapoint.onlog.post.dto.auth.TokensDto;
import keapoint.onlog.post.entity.Member;
import keapoint.onlog.post.repository.MemberRepository;
import keapoint.onlog.post.utils.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceUnitTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private BlogService blogService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    public void loginWithSocialAccount_ExistingUser_ReturnsPostLoginRes() throws Exception {
        // given: 테스트에 필요한 데이터를 정의합니다.
        String userName = "Test User";
        String userEmail = "test@test.com";
        String userProfileImg = "http://test.com/test.jpg";
        AccountType accountType = AccountType.KAKAO;

        // SocialAccountUserInfo 데이터와 Member 데이터를 생성합니다.
        SocialAccountUserInfo data = new SocialAccountUserInfo(userName, userEmail, userProfileImg);
        Member member = new Member();
        member.updateEmail(userEmail);

        // memberRepository.findByEmail(userEmail) 호출시 반환할 데이터를 정의합니다.
        when(memberRepository.findByEmail(userEmail)).thenReturn(Optional.of(member));

        // memberRepository.save(...) 호출시 반환할 데이터를 정의합니다.
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // jwtTokenProvider.createTokens(...) 호출시 반환할 데이터를 정의합니다.
        TokensDto tokens = new TokensDto("bearer", "accessToken", "refreshToken");
        when(jwtTokenProvider.createTokens(any(), any(), any())).thenReturn(tokens);

        // authenticationManagerBuilder.getObject() 호출시 반환할 데이터를 정의합니다.
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);

        // when: 테스트 대상 메소드를 호출합니다.
        PostLoginRes result = authService.loginWithSocialAccount(data, accountType);

        // then: 반환값을 검증합니다.
        assertEquals(userEmail, result.getEmail());
    }
}