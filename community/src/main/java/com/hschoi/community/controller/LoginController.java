package com.hschoi.community.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hschoi.community.annotation.SocialUser;
import com.hschoi.community.domain.User;
import com.hschoi.community.domain.enums.SocialType;

@Controller
public class LoginController {
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	//AOP or HandlerMethodArgumentResolver
	@GetMapping("/{facebook|google|kakao}/complete")
	public String loginComplete(@SocialUser User user) {
		return "redirect:/board/list";
	}
	
//	// 인증이 성공하면 리다이렉트되는 경로
//	@GetMapping("/{facebook|google|kakao}/complete")
//	public String loginComplete(HttpSession session) {
//		// SecurityContextHolder에서 인증된 정보를 OAuth2Authentication 형태로 받아옴
//		OAuth2Authentication authentication = (OAuth2Authentication)
//				SecurityContextHolder.getContext().getAuthentication();
//		// 리소스 서버에서 받아온 개인정보를 getDetails를 사용해 Map타입으로 받기
//		Map<String, String> map = (HashMap<String, String>)
//				authentication.getUserAuthentication().getDetails();
//		// 세션 빌더를 사용해 인증된 User 정보를 User 객체로 변환 저장
//		session.setAttribute("user", User.builder()
//			.name(map.get("name"))
//			.email(map.get("email"))
//			.principal(map.get("id"))
//			.socialType(SocialType.FACEBOOK)
//			.createdDate(LocalDateTime.now())
//			.build());
//		return "redirect:/board/list";
//	}
}
