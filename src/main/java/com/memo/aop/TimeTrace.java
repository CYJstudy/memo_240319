package com.memo.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeTrace {

	// PostController의 post-list-view에 annotation 붙여서 테스트해봄
	// 글목록이 표시될때만 실행시간이 표시 됨
}
