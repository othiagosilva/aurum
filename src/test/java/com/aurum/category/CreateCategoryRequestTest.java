
package com.aurum.category;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CreateCategoryRequestTest {

	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	static void setUp() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterAll
	static void tearDown() {
		factory.close();
	}

	@Test
	void shouldAcceptValidRequest() {
		var request = new CreateCategoryRequest("Alimentação", CategoryType.EXPENSE);

		Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

		assertTrue(violations.isEmpty());
	}

	@Test
	void shouldRejectBlankName() {
		var request = new CreateCategoryRequest("   ", CategoryType.EXPENSE);

		var violations = validator.validate(request);

		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
	}

	@Test
	void shouldRejectNameLongerThan100Characters() {
		var request = new CreateCategoryRequest("A".repeat(101), CategoryType.EXPENSE);

		var violations = validator.validate(request);

		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
	}

	@Test
	void shouldRejectNullType() {
		var request = new CreateCategoryRequest("Alimentação", null);

		var violations = validator.validate(request);

		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")));
	}
}
