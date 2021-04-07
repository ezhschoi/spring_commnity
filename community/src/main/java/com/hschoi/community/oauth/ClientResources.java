package com.hschoi.community.oauth;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;

public class ClientResources {
	//@NestedConfigurationProperty는 해당필드가 단일값이 아닌 중복으로 바인딩 된다고 표시하는 어노테이션
	@NestedConfigurationProperty
	private AuthorizationCodeResourceDetails client = 
			new AuthorizationCodeResourceDetails();
	
	// 
	@NestedConfigurationProperty
	private ResourceServerProperties resource = 
			new ResourceServerProperties();
	
	public AuthorizationCodeResourceDetails getClient() {
		return client;
	}
	
	public ResourceServerProperties getResource() {
		return resource;
	}
}
