package com.woolbro.controller;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.woolbro.exception.AppException;
import com.woolbro.model.Role;
import com.woolbro.model.RoleName;
import com.woolbro.model.User;
import com.woolbro.payload.ApiResponse;
import com.woolbro.payload.JwtAuthenticationResponse;
import com.woolbro.payload.LoginRequest;
import com.woolbro.payload.SignUpRequest;
import com.woolbro.repository.RoleRepository;
import com.woolbro.repository.UserRepository;
import com.woolbro.security.JwtTokenProvider;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtTokenProvider tokenProvider;
	

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		
		// Authentication Manager는 사용자 로그인 정보가 올바른지를 검사하고, 
		// 올바를 경우 Token을 생성하고 값을 넘겨주는 반면 틀릴 경우 인증 실패도 알려야 합니다.
        // AuthenticationManager 에 token 을 넘기면 UserDetailsService 가 받아 처리
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsernameOrEmail(), 
						loginRequest.getPassword()
						)
				);
		
        // SecurityContext 에 authentication 정보를 등록한다.
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = tokenProvider.generateToken(authentication);
		
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}
	
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
		
		//userRepository에서 Username 존재여부를 검사합니다(existsByUsername)
		if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
		
        //존재하지 않으면 User 객체를 신규로 생성합니다.
		User user = new User(signUpRequest.getName(),signUpRequest.getUsername(),
				signUpRequest.getEmail(), signUpRequest.getPassword());
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		// 권한(Role)도 생성합니다
		Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(()-> new AppException("User Role not set."));
		
		user.setRoles(Collections.singleton(userRole));
		
		User result = userRepository.save(user);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(result.getUsername())
				.toUri();
		
		return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
		
	}
	
	
}
