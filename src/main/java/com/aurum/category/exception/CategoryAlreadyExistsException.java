package com.aurum.category.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
	public CategoryAlreadyExistsException() {
		super("A categoria já existe");
	}
}