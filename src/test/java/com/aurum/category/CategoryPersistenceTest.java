package com.aurum.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.aurum.AbstractIntegrationTest;

import jakarta.persistence.EntityManager;

class CategoryPersistenceTest extends AbstractIntegrationTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@Transactional
	void shouldPersistAndRetrieveCategory() {
		Category category = new Category("Alimentação-", CategoryType.EXPENSE);

		entityManager.persist(category);
		entityManager.flush();

		UUID categoryId = category.getId();

		entityManager.clear();

		Category persistedCategory = entityManager.find(Category.class, categoryId);

		assertThat(persistedCategory).isNotNull();
		assertThat(persistedCategory.getId()).isEqualTo(categoryId);
		assertThat(persistedCategory.getName()).isEqualTo("Alimentação-");
		assertThat(persistedCategory.getType()).isEqualTo(CategoryType.EXPENSE);
		assertThat(persistedCategory.getCreatedAt()).isNotNull();
	}

	@Test
	@Transactional
	void shouldRejectCategoriesWithSameNameAndTypeIgnoringCase() {
		UUID firstId = UUID.randomUUID();
		UUID duplicateId = UUID.randomUUID();

		jdbcTemplate.update("""
				INSERT INTO categories (id, name, type)
				VALUES (?, ?, ?)
				""", firstId, "Alimentação", "EXPENSE");

		assertThatThrownBy(() -> jdbcTemplate.update("""
				INSERT INTO categories (id, name, type)
				VALUES (?, ?, ?)
				""", duplicateId, "alimentação", "EXPENSE")).isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@Transactional
	void shouldPersistCategoryDeactivation() {

		Category category = new Category("Alimentação", CategoryType.EXPENSE);

		Category savedCategory = categoryRepository.saveAndFlush(category);
		UUID categoryId = savedCategory.getId();

		categoryService.delete(categoryId);

		entityManager.flush();
		entityManager.clear();

		Category persistedCategory = categoryRepository.findById(categoryId).orElseThrow();

		assertFalse(persistedCategory.isActive());
		assertEquals(categoryId, persistedCategory.getId());

	}
}