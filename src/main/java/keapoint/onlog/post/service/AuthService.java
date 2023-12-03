package keapoint.onlog.post.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import keapoint.onlog.post.base.AccountType;
import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.Role;
import keapoint.onlog.post.dto.auth.PostLoginRes;
import keapoint.onlog.post.dto.auth.PostLogoutRes;
import keapoint.onlog.post.dto.auth.SocialAccountUserInfo;
import keapoint.onlog.post.dto.auth.TokensDto;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
import keapoint.onlog.post.entity.Member;
import keapoint.onlog.post.repository.MemberRepository;
import keapoint.onlog.post.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    private final BlogService blogService;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId; // Rest Api Key

    /**
     * 카카오 인가 코드로 카카오 access token 발급받기
     *
     * @param authCode Kakao auth code
     * @return kakao access token
     */
    public String getKakaoAccessToken(String authCode) throws BaseException {
        try {
            // webClient 설정
            WebClient kakaoWebClient =
                    WebClient.builder()
                            .baseUrl("https://kauth.kakao.com/oauth/token")
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                            .build();

            // token api 호출
            Map<String, Object> tokenResponse =
                    kakaoWebClient
                            .post()
                            .uri(uriBuilder -> uriBuilder
                                    .queryParam("grant_type", "authorization_code")
                                    .queryParam("client_id", clientId)
                                    .queryParam("code", authCode)
                                    .build())
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

            return (String) tokenResponse.get("access_token");

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 카카오 access token으로 사용자의 정보 발급받기
     *
     * @param accessToken Kakao access token
     * @return 사용자의 이름과 메일이 들어있는 객체
     */
    public SocialAccountUserInfo getKakaoUserInfo(String accessToken) throws BaseException {
        try {
            // webClient 설정
            WebClient kakaoApiWebClient =
                    WebClient.builder()
                            .baseUrl("https://kapi.kakao.com/v2/user/me")
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                            .build();

            String response =
                    kakaoApiWebClient
                            .post()
                            .header("Authorization", "Bearer " + accessToken)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

            log.info("User Information Request Results : " + response);

            JsonObject kakaoAccount = JsonParser.parseString(response)
                    .getAsJsonObject()
                    .get("kakao_account")
                    .getAsJsonObject();

            // 닉네임 정보 담기
            String username = kakaoAccount.get("profile")
                    .getAsJsonObject()
                    .get("nickname")
                    .getAsString();

            // 이메일 정보 담기
            String email;
            if (kakaoAccount.get("has_email").getAsBoolean()) {
                email = kakaoAccount.get("email").getAsString();
                log.info("사용자 email: " + email);

            } else { // 이메일이 없는 경우 Exception. 이메일이 사용자의 식별자로 사용되고 있기 때문에 무조건 필요함
                throw new BaseException(BaseErrorCode.EMAIL_NOT_FOUND_EXCEPTION);
            }

            // 프로필 이미지 권한이 있는 경우 해당 이미지를 사용하고
            // 프로필 이미지 권한이 없는 경우 null로 설정한다.
            boolean needProfileImageAgreement = kakaoAccount
                    .get("profile_image_needs_agreement")
                    .getAsBoolean();
            log.info("사용자 프로필 이미지 동의 여부: " + needProfileImageAgreement);

            String profileImgUrl; // 사용자 프로필 이미지 url
            if (needProfileImageAgreement) {
                profileImgUrl = null;

            } else {
                profileImgUrl = kakaoAccount
                        .get("profile")
                        .getAsJsonObject()
                        .get("profile_image_url")
                        .getAsString();
                log.info("사용자 프로필 이미지 url: " + profileImgUrl);
            }

            return SocialAccountUserInfo.builder()
                    .userName(username)
                    .userEmail(email)
                    .profileImgUrl(profileImgUrl)
                    .build();

        } catch (BaseException exception) {
            throw exception;

        } catch (Exception exception) {
            log.error("Exception in get Kakao user info : " + exception.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 로그인 - 소셜 계정
     */
    @Transactional(rollbackFor = BaseException.class)
    public PostLoginRes loginWithSocialAccount(SocialAccountUserInfo data, AccountType type) throws BaseException {
        try {
            // 이메일을 기반으로 사용자를 조회한다
            Optional<Member> memberByEmail = memberRepository.findByEmail(data.getUserEmail());

            Member member;
            if (memberByEmail.isEmpty()) { // 사용자 정보가 없는 경우
                // 사용자를 생성하고
                Member newMember = Member.builder()
                        .email(data.getUserEmail())
                        .password(passwordEncoder.encode(data.getUserEmail()))
                        .phoneNumber(null)
                        .agreePersonalInfo(false)
                        .agreePromotion(false)
                        .refreshToken(null)
                        .role(Role.USER)
                        .accountType(type)
                        .userName(data.getUserName())
                        .build();

                // DB에 사용자 정보를 저장한다.
                member = memberRepository.save(newMember);
                log.info("생성된 사용자 정보: " + newMember);

                // 사용자의 블로그를 생성
                blogService.createBlog(new PostCreateBlogReqDto(member.getMemberIdx(), data.getUserName(), member.getMemberIdx().toString(), "", data.getProfileImgUrl()));

            } else { // 사용자 정보가 있는 경우
                member = memberByEmail.get();
            }

            // --- 로그인 처리 ---
            // 토큰을 발급받고, refresh token을 DB에 저장한다.
            TokensDto token = generateToken(member.getEmail(), member.getEmail(), member.getMemberIdx(), member.getPassword());
            member.updateRefreshToken(token.getRefreshToken());

            // 사용자 정보 로깅
            log.info(member.toString());

            return new PostLoginRes(member.getMemberIdx(), member.getEmail(), data.getProfileImgUrl(), token);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 토큰 발행
     *
     * @param principal   로그인 시도 아이디
     * @param credentials 로그인 시도 비밀번호
     * @param memberIdx   사용자 식별자
     * @param password    사용자 비밀번호
     * @return 토큰이 들어있는 객체
     */
    public TokensDto generateToken(Object principal, Object credentials, UUID memberIdx, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, credentials);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return jwtTokenProvider.createTokens(authentication, memberIdx, password);
    }

    /**
     * 사용자 로그아웃
     *
     * @param token 사용자 token
     * @return 사용자 식별자
     */
    public PostLogoutRes logout(String token) throws BaseException {
        try {
            // 사용자의 식별자 추출
            String memberIdx = jwtTokenProvider.extractIdx(token);
            log.info("로그아웃 하는 사용자의 식별자: " + memberIdx);

            // 사용자 정보 가져오기
            Member member = memberRepository.findById(UUID.fromString(memberIdx)).
                    orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND_EXCEPTION));
            log.info("로그아웃 하는 사용자 정보: " + member.toString());

            // 사용자의 refresh token 파기
            member.invalidateRefreshToken();
            log.info("사용자 (" + member.getEmail() + ")의 refresh token 파기 완료");

            return new PostLogoutRes(true);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
