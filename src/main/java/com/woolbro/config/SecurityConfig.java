package com.woolbro.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.woolbro.security.CustomUserDetailsService;
import com.woolbro.security.JwtAuthenticationEntryPoint;
import com.woolbro.security.JwtAuthenticationFilter;


// (extends) WebSecurityConfigurerAdapter
// 이 클래스는 Spring Security의 WebSecurityConfigurer 인터페이스를 구현합니다. 기본 보안 구성을 제공합니다.
// 사용자 정의 보안 구성을 제공하기 위해서 메소드를 확장하고 재정의 합니다.
@Configuration 
@EnableWebSecurity //프로젝트에서 웹 보안을 가능하게 하는 기본 Spring Security Annotation 입니다.
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true) // 메소드 보안을 위해 사용합니다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	// 이 클래스틑 인증절차 없이 자원에 엑세스 하려고 시도하는 클라이언트에게 401 오류를 반환하는데 사용됩니다.
	// Sprint Security의 AuthenticationEntyPoint 인터페이스를 구현합니다.
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHanler;
	
	//JWT인증 토큰을 읽고, 유효성을 검사하고, 토큰과 관련된 세부사항을 로드합니다.
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}
	
	// 사용자 인증을 위한 Spring Security 를 생성하는데 사용됩니다. 
	// 메모리 내 인증, JDBC 인증, 사용자 정의 인증 등을 사용 할 수 있지만, 
	// 이 예시에서는 passwordEncoder를 사용했습니다.
	@Override
	protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder
		.userDetailsService(customUserDetailsService)
		.passwordEncoder(passwordEncoder());
	}
	
	
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// HttpSecurity 구성과 같은 보안기능
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors()
				.and()
			.csrf()
				.disable()
			.exceptionHandling()
				.authenticationEntryPoint(unauthorizedHanler)
				.and()
			.sessionManagement()
			// stateless session exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.authorizeRequests()
				.antMatchers("/",
						"/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
						.permitAll()
					.antMatchers("/api/auth/**")
						.permitAll()
	                .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
	                	.permitAll()
	                .antMatchers(HttpMethod.GET, "/api/polls/**", "/api/users/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated();
		
		
        // Add our custom JWT security filter
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
				
	}
	
	

}
