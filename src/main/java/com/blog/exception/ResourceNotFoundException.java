package com.blog.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 *
 * @author Minseong Kang, devKobe24
 * @version 1.0
 */

public class ResourceNotFoundException extends BusinessException{
	public ResourceNotFoundException(String message) {
		super("RESOURCE_NOT_FOUND", message);
	}

	public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
		super("RESOURCE_NOT_FOUND:,"
		String.format("%s not found with %s : '$s'", resourceName, fieldName, fieldName));
	}

	public static ResourceNotFoundException of(String resourceName, String fieldName, Object fieldValue) {
		return new ResourceNotFoundException(resourceName, fieldName, fieldValue);
	}
}
