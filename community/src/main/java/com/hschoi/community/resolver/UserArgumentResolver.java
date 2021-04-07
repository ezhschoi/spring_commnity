package com.hschoi.community.resolver;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.hschoi.community.annotation.SocialUser;
import com.hschoi.community.domain.User;
import com.hschoi.community.domain.enums.SocialType;
import com.hschoi.community.repository.UserRepository;
import static com.hschoi.community.domain.enums.SocialType.FACEBOOK;
import static com.hschoi.community.domain.enums.SocialType.GOOGLE;
import static com.hschoi.community.domain.enums.SocialType.KAKAO;;

@Component	//Rosolver에 잊지 말 것
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
	
	private UserRepository userRepository;
	
	public UserArgumentResolver(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * HandlerMethodArgumentResolver가 해당하는 파라미터를 지원할지 여부를 반환
	 * true를 반환하면 resolveArgument 메소드가 수행
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(SocialUser.class) != null &&
				parameter.getParameterType().equals(User.class);
	}

	/**
	 * 파라미터 인자값에 대한 정보를 바탕을 실제 객체를 생성하여 해당 파라미터 객체를 바인딩
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpSession session = ((ServletRequestAttributes) 
				RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
		User user = (User) session.getAttribute("user");
		return getUser(user, session);
	}

	/**
	 * 인증된 User 객체를 생성
	 * @param user
	 * @param session
	 * @return
	 */
	private User getUser(User user, HttpSession session) {
		if (user == null) {
			try {
				// 인증된 OAuth2Authentication 객체를 가져옴
				OAuth2Authentication authentication = (OAuth2Authentication)
						SecurityContextHolder.getContext().getAuthentication();
				// 사용자 정보를 Map에 넣음
				Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
				// User객체로 변경
				User convertUser = convertUser(String.valueOf(
						authentication.getAuthorities().toArray()[0]), map);
				if (user == null) {
					user = userRepository.save(convertUser);
				}
				
			} catch(ClassCastException e) {
				return user;
			}
		}
		return user;
	}
	
	/**
	 * 사용자의 인증된 소셜 미디어 타입에 따라 빌더를 사용하여 User객체를 생성하는 Bridge
	 * @param authority
	 * @param map
	 * @return
	 */
	private User convertUser(String authority, Map<String, String> map) {
		if (FACEBOOK.isEquals(authority)) return getModernUser(FACEBOOK, map);
		else if (GOOGLE.isEquals(authority)) return getModernUser(GOOGLE, map);
		else if (KAKAO.isEquals(authority)) return getKakaoUser(map);
		return null;
	}
	
	/**
	 * 페이스북이나 구글과 같이 공통된 명명규칙을 가진 그룹을 User객체로 매핑
	 * @param socialType
	 * @param map
	 * @return
	 */
	private User getModernUser(SocialType socialType, Map<String, String> map) {
		return User.builder()
				.name(map.get("name"))
				.email(map.get("email"))
				.principal(map.get("principal"))
				.socialType(socialType)
				.createdDate(LocalDateTime.now())
				.build();
	}
	
	/**
	 * 카카오를 위한 매핑 메소드
	 * @param map
	 * @return
	 */
	private User getKakaoUser(Map<String, String> map) {
		HashMap<String, String> propertyMap = (HashMap<String, String>)(Object)
				map.get("properties");
		return User.builder()
				.name(propertyMap.get("nickname"))
				.email(map.get("kaccount_email"))
				.principal(String.valueOf(map.get("id")))
				.socialType(KAKAO)
				.createdDate(LocalDateTime.now())
				.build();
	}
	
	/**
	 * 인증된 authentication이 권한을 가지고 있는지 체크
	 * 저장된 User 권한이 없으면 SecurityContextHolder를 사용해 
	 * 대상 소셜 미디어타입으로 권한 저장
	 * @param user
	 * @param authentication
	 * @param map
	 */
	private void setRoleIfNotSame(User user, OAuth2Authentication authentication,
			Map<String, String> map) {
		if(!authentication.getAuthorities().contains(new 
				SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {
			SecurityContextHolder.getContext().setAuthentication(new
					UsernamePasswordAuthenticationToken(map, "N/A", 
							AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
			
		}
	}
}
