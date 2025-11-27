package com.example.spring.common.annotation.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Target({ElementType.METHOD})	// 메서드에만 적용되는 Annotation
@Retention(RetentionPolicy.RUNTIME)	// 적용 타이밍 -> Runtimeㅇ 적용
@RequestBody
public @interface ApiRequest {
	Content[] content() default {};
	boolean required() default false;
	String description() default "";
}
