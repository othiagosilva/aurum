package com.aurum.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aurum.AbstractIntegrationTest;
import com.aurum.category.Category;
import com.aurum.category.CategoryType;

import jakarta.persistence.EntityManager;

public class TransactionPersistenceTest extends AbstractIntegrationTest {
	@Autowired
	private EntityManager entityManager;

	@Test
	@Transactional
	void shouldPersistAndRetrieveTransaction() {
		Category category = new Category("Restaurante", CategoryType.EXPENSE);
		Transaction transaction = new Transaction("Verace", new BigDecimal("150.26"), TransactionType.EXPENSE,
				LocalDate.now(), category);

		entityManager.persist(category);
		entityManager.persist(transaction);

		entityManager.flush();

		UUID transactionId = transaction.getId();

		entityManager.clear();

		Transaction persistedTransaction = entityManager.find(Transaction.class, transactionId);

		assertThat(persistedTransaction).isNotNull();
		assertThat(persistedTransaction.getId()).isEqualTo(transactionId);
		assertThat(persistedTransaction.getDescription()).isEqualTo("Verace");
		assertThat(persistedTransaction.getAmount()).isEqualTo(new BigDecimal("150.26"));
		assertThat(persistedTransaction.getType()).isEqualTo(TransactionType.EXPENSE);
		assertThat(persistedTransaction.getTransactionDate()).isEqualTo(transaction.getTransactionDate());
		assertThat(persistedTransaction.getCategory().getId()).isEqualTo(category.getId());
	}
}
