package com.woolbro.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


// AuthenticationEntryPoint 인터페이스를 사용함으로, 
// 인증이 필요한 resource에 엑세스 하려고 시도 할 때에 호출하게 되는데요,
// 그 중에서 예외가 발생 할 때마다 이 메소드가 호출됩니다.

// 이 클래스틑 인증절차 없이 자원에 엑세스 하려고 시도하는 클라이언트에게 401 오류를 반환하는데 사용됩니다.
// Sprint Security의 AuthenticationEntyPoint 인터페이스를 구현합니다.

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

	@Override
	public void commence(HttpServletRequest httpServletRequest, 
			HttpServletResponse httpServletResponse,
			AuthenticationException e) throws IOException, ServletException {
		logger.error("Responding with unauthorized error. Message - {}", e.getMessage());
		httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
	}
}
