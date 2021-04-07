package com.hschoi.community.oauth;

import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.hschoi.community.domain.enums.SocialType;

public class UserTokenService extends UserInfoTokenServices {

	public UserTokenService(ClientResources resources, SocialType socialType) {
		// User 정보를 얻어오기 위해 소셜 서버와 통신 역할
		super(resources.getResource().getUserInfoUri(), 
				resources.getClient().getClientId());
		setAuthoritiesExtractor(new OAuth2AuthoritiesExtractor(socialType));
	}
	
	public static class OAuth2AuthoritiesExtractor implements AuthoritiesExtractor {
		private String socialType;
		
		public OAuth2AuthoritiesExtractor(SocialType socialType) {
			this.socialType = socialType.getRoleType();
		}

		/**
		 * 권한을 목록 형태로 생성하여 반환 
		 */
		@Override
		public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
			return AuthorityUtils.createAuthorityList(this.socialType);
		}
	}
}
