package com.hschoi.community.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import com.hschoi.community.domain.enums.SocialType;
import com.hschoi.community.oauth.ClientResources;
import com.hschoi.community.oauth.UserTokenService;

import static com.hschoi.community.domain.enums.SocialType.FACEBOOK;
import static com.hschoi.community.domain.enums.SocialType.GOOGLE;
import static com.hschoi.community.domain.enums.SocialType.KAKAO;;

@Configuration
@EnableWebSecurity	//시큐리티 기능을 사용하겠다는 어노테이션
// WebSecurityConfigurerAdapter를 상속 받고 configure 오버리아드
//OAuth2: @EnableOAuth2Client	//@EnableOAuthorizationServer, @EnabledResourceServer도 있음(서버직접 구성할 경우 필요)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	//OAuth2: @Autowired
	//OAuth2: private OAuth2ClientContext oAuth2ClientContext;
	
	// authorizeRequests() 인증 메커니즘을 요청한 HttpServletRequest기반으로 설정
	// - antMatchers(): 요청 패턴을 리스트 형식으로 설정
	// - permitAll(): 설정한 리퀘스트 패턴을 누구나 접근할 수 있도록 허용
	// - anyRequest(): 설정한 요청 이외의 리퀘스트 요청을 표현
	// - authenticated(): 해당 요청은 인증된 사용자만 사용
	// headers(): 응답에 해당하는 header를 설정. 설정하지 않으면 디폴트값으로 설정
	// - frameOptions().disabled(): XFrameOptionsHeaderWriter의 최적화 설정을 허용하지 않음
	// authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")):
	// 	인증의 진입지점. 인증되지 않은 사용자가 허용되지 않은 경로로 리퀘스트를 요청할 경우 '/login'으로 이동
	// formLogin().successForwardUrl("/board/list"): 로그인에 성공하면 설정된 경로로 포워딩
	// logout(): 로그아웃에 대한 설정 로그아웃이 수행될 URL(logoutUrl).
	// 로그아웃이 성공했을 때 포워딩될 URL(logoutSuccessUrl) 로그아웃이 성공했을 때 삭제될 쿠키값(deleteCookies)
	// 설정된 세션의 무효화(invalidateHttpSession)를 수행하게끔 설정
	// addFilterBefore(filter, beforeFilter): 첫번째 인자보다 먼저 시작될 필터를 등록
	// - addFilterBefore(filter, CsrfFilter.class): 문자 인코딩(filter)보다 CsrfFilter를 먼저 실행하도록 설정
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		http
			.authorizeRequests()
				.antMatchers("/", "/oauth2/**", "/login/**", "/css/**",
						"/images/**", "/js/**", "/console/**").permitAll()
				// 소셜 미디어 경로 지정
				.antMatchers("/facebook").hasAnyAuthority(FACEBOOK.getRoleType())
				.antMatchers("/google").hasAnyAuthority(GOOGLE.getRoleType())
				.antMatchers("/kakao").hasAnyAuthority(KAKAO.getRoleType())
				.anyRequest().authenticated()
			.and()
				.oauth2Login()
				.defaultSuccessUrl("/loginSuccess")
				.failureUrl("/loginFailure")
			.and()
				.headers().frameOptions().disable()
			.and()
				.exceptionHandling()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
			.and()
				.formLogin()
				.successForwardUrl("/board/list")
			.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
			.and()
				.addFilterBefore(filter, CsrfFilter.class)
				//OAuth2: .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
				.csrf().disable();
	}
	
	/**
	 * 
	 */
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository {
		OAuth2ClientProperties oAuth2ClientProperties, @Value(
				"${custom.oauth2.kakao.client-id}") String kakaoClientId) {
					
				}
	}
	
	/**
	 * OAuth2 클라이언트용 시큐리티 필터인 OAuth2ClientContextFilter를 불러와서
	 * 올바른 순서로 필터가 동작하도록 설정
	 * 스프링 시큐리티 필터가 실행되기 전에 충분히 낮은 순서로 필터로 등록
	 * @param filter
	 * @return
	 */
//OAuth2: 
//	@Bean
//	public FilterRegistrationBean oauth2ClientRegistration(
//			OAuth2ClientContextFilter filter) {
//		FilterRegistrationBean registration = new FilterRegistrationBean();
//		registration.setFilter(filter);
//		registration.setOrder(-100);
//		return registration;
//	}
	
	/**
	 * 각 소셜미디어 필터를 리스트 형식으로 한꺼번에 설정하여 반환
	 * @return
	 */
	//OAuth2: 
//	private Filter oauth2Filter() {
//		CompositeFilter filter = new CompositeFilter();
//		List<Filter> filters = new ArrayList<>();
//		filters.add(oauth2Filter(facebook(), "/login/facebook", FACEBOOK));
//		filters.add(oauth2Filter(google(), "/login/google", GOOGLE));
//		filters.add(oauth2Filter(kakao(), "/login/kakao", KAKAO));
//		filter.setFilters(filters);
//		return filter;
//	}
	
	/**
	 * 각 소셜미디어 타입을 받아서 필터 설정
	 * @param client
	 * @param path
	 * @param socialType
	 * @return
	 */
//OAuth2: 
//	private Filter oauth2Filter(ClientResources client, String path,
//			SocialType socialType) {
//		// 인증이 수행될 경로를 넣어 OAuth2클라이언트용 인증 처리 필터 생성
//		OAuth2ClientAuthenticationProcessingFilter filter = 
//				new OAuth2ClientAuthenticationProcessingFilter(path);
//		// 권한 서버와의 통신을 위해 OAuth2RestTemplate를 생성
//		// 이를 위해 client 프로퍼티 정보와 OAuth2ClientContext가 필요
//		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(),
//				oAuth2ClientContext);
//		filter.setRestTemplate(template);
//		// User의 권한으 최적화해서 생성하고자 UserInfoTokentServices를 상속받은 UserTokenService를 생성
//		// OAuth2 Access Token 검증을 위해 생성한 UserTokenService를 필터의 토큰 서비스로 등록
//		filter.setTokenServices(new UserTokenService(client, socialType));
//		// 인증 성공
//		filter.setAuthenticationSuccessHandler((request, response, authentication) ->
//				response.sendRedirect("/" + socialType.getValue() + 
//						"/complete"));
//		// 인증 실패
//		filter.setAuthenticationFailureHandler((request, response, exception) ->
//				response.sendRedirect("/error"));
//		return filter;
//	}

//OAuth2: 
//	@Bean
//	@ConfigurationProperties("facebook")
//	public ClientResources facebook()
//	{
//		return new ClientResources();
//	}
//	
//	@Bean
//	@ConfigurationProperties("google")
//	public ClientResources google()
//	{
//		return new ClientResources();
//	}
//	
//	@Bean
//	@ConfigurationProperties("kakao")
//	public ClientResources kakao()
//	{
//		return new ClientResources();
//	}
}
