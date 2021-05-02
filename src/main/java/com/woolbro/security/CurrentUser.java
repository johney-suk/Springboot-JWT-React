package com.woolbro.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) // RUNTIME은 자바가 VM에서 실행되는 동안에도 유지되는 것입니다.
@Documented //javadoc2으로 api 문서를 만들 때 어노테이션에 대한 설명도 포함하도록 지정해주는 것
@AuthenticationPrincipal //로그인한 사용자의 정보를 파라메터로 받고 싶을때 기존에는 다음과 같이 Principal 객체로 받아서 사용한다.
public @interface CurrentUser {

}
