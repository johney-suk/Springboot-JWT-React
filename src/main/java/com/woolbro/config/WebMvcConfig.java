package com.woolbro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// (Cross-Origin Resource Sharing,CORS)란 다른 출처의 자원을 공유할 수 있도록 설정하는 권한 체제
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	public final long MAX_AGE_SECS = 3600;
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") //CORS를 적용할 URL패턴을 지정
				.allowedOrigins("*") // 자원 공유를 허락할 Origin을 지정
				.allowedMethods("HEAD","OPIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
				.maxAge(MAX_AGE_SECS); // pre-flight 리퀘스트를 캐싱
		
	}
}
