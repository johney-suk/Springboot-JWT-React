package com.woolbro.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

// 사용자가 성공적으로 로그인 한 후 JWT를 생성하고, JWT의 유효성을 검사하는데 사용 됩니다.
@Component
public class JwtTokenProvider {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(JwtTokenProvider.class);
	
	@Value("${app.jwtSecret}")
	private String jwtSecret; // 암호화 키
	
	@Value("${app.jwtExpirationInMs}")
	private int jwtExpirationInMs; // 만료일 상수

	public String generateToken(Authentication authentication) { // JWT 생성
		
		UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
		
		Date now = new Date();
		Date expirydate = new Date(now.getTime() + jwtExpirationInMs);
		
		return Jwts.builder()
				.setSubject(Long.toString(userPrincipal.getId())) // 들어갈 sub
				.setIssuedAt(new Date()) // 생성일
				.setExpiration(expirydate) // 만료일
				.signWith(SignatureAlgorithm.HS512, jwtSecret) // 암호화 방식
				.compact(); // 토큰 생성 메소드
	}
	
	public Long getUserIdFromJWT(String token) { // JWT로 부터 UserId 획득
		Claims claims =
				Jwts
				.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(token)
				.getBody();
		
		return Long.parseLong(claims.getSubject());
	}
	
	public boolean validateToken(String authToken) { // JWT 유효성 검사
		
		try {
				Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
				return true;
			} catch (SignatureException ex) { 
				logger.error("시그너처 연산이 실패하였거나, JWT의 시그너처 검증이 실패한 경우"); 
			} catch (MalformedJwtException ex) { 
				logger.error("구조적인 문제가 있는 JWT"); 
			} catch (ExpiredJwtException ex) { 
				logger.error("유효기간 지난 JWT수신"); 
			} catch (UnsupportedJwtException ex) { 
				logger.error("Unsupported JWT token"); 
			} catch (IllegalArgumentException ex) { 
				logger.error("JWT claims string is empty."); 
			} 
			return false; 
		}
}
