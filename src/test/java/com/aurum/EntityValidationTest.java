
package com.aurum;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.aurum.category.Category;
import com.aurum.category.CategoryType;
import com.aurum.transaction.Transaction;
import com.aurum.transaction.TransactionType;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class EntityValidationTest {

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

	// CATEGORY

	@Test
	void shouldAcceptValidCategory() {
		Category category = new Category("Alimentação", CategoryType.EXPENSE);

		Set<ConstraintViolation<Category>> violations = validator.validate(category);

		assertThat(violations).isEmpty();
	}

	@Test
	void shouldRejectCategoryWithBlankName() {
		Category category = new Category("   ", CategoryType.EXPENSE);

		Set<ConstraintViolation<Category>> violations = validator.validate(category);

		assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("name");
	}

	@Test
	void shouldRejectCategoryWithNameLongerThan100Characters() {
		Category category = new Category("A".repeat(101), CategoryType.EXPENSE);

		Set<ConstraintViolation<Category>> violations = validator.validate(category);

		assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("name");
	}

	@Test
	void shouldRejectCategoryWithoutType() {
		Category category = new Category("Alimentação", null);

		Set<ConstraintViolation<Category>> violations = validator.validate(category);

		assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("type");
	}

	// TRANSACTION

	@Test
	void shouldAcceptValidTransaction() {
		Category category = new Category("Alimentação", CategoryType.EXPENSE);

		Transaction transaction = new Transaction("Compra no supermercado", new BigDecimal("150.25"),
				TransactionType.EXPENSE, LocalDate.of(2026, 10, 17), category);

		Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

		assertThat(violations).isEmpty();
	}

	@Test
	void shouldRejectTransactionWithBlankDescription() {
		Transaction transaction = createTransaction("   ", new BigDecimal("100.00"), TransactionType.EXPENSE,
				LocalDate.now(), validCategory());

		assertInvalidProperty(transaction, "description");
	}

	@Test
	void shouldRejectTransactionWithDescriptionLongerThan200Characters() {
		Transaction transaction = createTransaction("A".repeat(201), new BigDecimal("100.00"), TransactionType.EXPENSE,
				LocalDate.now(), validCategory());

		assertInvalidProperty(transaction, "description");
	}

	@Test
	void shouldRejectTransactionWithNullAmount() {
		Transaction transaction = createTransaction("Compra", null, TransactionType.EXPENSE, LocalDate.now(),
				validCategory());

		assertInvalidProperty(transaction, "amount");
	}

	@Test
	void shouldRejectTransactionWithZeroAmount() {
		Transaction transaction = createTransaction("Compra", BigDecimal.ZERO, TransactionType.EXPENSE, LocalDate.now(),
				validCategory());

		assertInvalidProperty(transaction, "amount");
	}

	@Test
	void shouldRejectTransactionWithNegativeAmount() {
		Transaction transaction = createTransaction("Compra", new BigDecimal("-10.00"), TransactionType.EXPENSE,
				LocalDate.now(), validCategory());

		assertInvalidProperty(transaction, "amount");
	}

	@Test
	void shouldRejectTransactionWithMoreThanTwoDecimalPlaces() {
		Transaction transaction = createTransaction("Compra", new BigDecimal("10.123"), TransactionType.EXPENSE,
				LocalDate.now(), validCategory());

		assertInvalidProperty(transaction, "amount");
	}

	@Test
	void shouldRejectTransactionWithNullType() {
		Transaction transaction = createTransaction("Compra", new BigDecimal("100.00"), null, LocalDate.now(),
				validCategory());

		assertInvalidProperty(transaction, "type");
	}

	@Test
	void shouldRejectTransactionWithNullDate() {
		Transaction transaction = createTransaction("Compra", new BigDecimal("100.00"), TransactionType.EXPENSE, null,
				validCategory());

		assertInvalidProperty(transaction, "transactionDate");
	}

	@Test
	void shouldRejectTransactionWithoutCategory() {
		Transaction transaction = createTransaction("Compra", new BigDecimal("100.00"), TransactionType.EXPENSE,
				LocalDate.now(), null);

		assertInvalidProperty(transaction, "category");
	}

	private static Category validCategory() {
		return new Category("Alimentação", CategoryType.EXPENSE);
	}

	private static Transaction createTransaction(String description, BigDecimal amount, TransactionType type,
			LocalDate transactionDate, Category category) {

		return new Transaction(description, amount, type, transactionDate, category);
	}

	private static void assertInvalidProperty(Transaction transaction, String property) {

		Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

		assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains(property);
	}
}
