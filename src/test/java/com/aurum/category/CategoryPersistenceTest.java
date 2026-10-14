package com.aurum.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.aurum.AbstractIntegrationTest;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

class CategoryPersistenceTest extends AbstractIntegrationTest {

	@Autowired
	private EntityManager entityManager;

	@Test
	@Transactional
	void shouldPersistAndRetrieveCategory() {
		Category category = new Category("Alimentação", CategoryType.EXPENSE);

		entityManager.persist(category);
		entityManager.flush();

		UUID categoryId = category.getId();

		entityManager.clear();

		Category persistedCategory = entityManager.find(Category.class, categoryId);

		assertThat(persistedCategory).isNotNull();
		assertThat(persistedCategory.getId()).isEqualTo(categoryId);
		assertThat(persistedCategory.getName()).isEqualTo("Alimentação");
		assertThat(persistedCategory.getType()).isEqualTo(CategoryType.EXPENSE);
		assertThat(persistedCategory.getCreatedAt()).isNotNull();
	}
}