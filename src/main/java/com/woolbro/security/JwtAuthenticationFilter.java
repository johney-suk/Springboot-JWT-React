package com.woolbro.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


// AuthTokenFilter가 Request로 넘어온 정보를 가지고 로그인 여부를 검사
// JWT인증 토큰을 읽고, 유효성을 검사하고, 토큰과 관련된 세부사항을 로드합니다.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	private static final Logger logger = 
			LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = getjwtFromRequest(request);
			
			// 토큰이 발급되어 있을 경우
			// 토큰기간이 유효
			if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				Long userId = tokenProvider.getUserIdFromJWT(jwt);
				
				UserDetails userDetails = customUserDetailsService.loadUserById(userId);
				
				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					//현재 접속한 유저의 정보를 가져온다.
					SecurityContextHolder.getContext().setAuthentication(authentication); 
			}
		} catch (Exception ex) {
			logger.error("Could not set user authentication in security context",ex);
		}
		
		filterChain.doFilter(request, response);
	}

	
	private String getjwtFromRequest(HttpServletRequest request) {
		
		String bearerToken = request.getHeader("Authorization"); // 암호화하지 않은 전달 토큰
		
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		
		return null;
	}

}
