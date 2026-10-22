package com.aurum.category.exception;

public class CategoryIsNotActiveException extends RuntimeException {
	public CategoryIsNotActiveException() {
		super("A categoria não está ativa. Não é permitido realizar alterações.");
	}
}
