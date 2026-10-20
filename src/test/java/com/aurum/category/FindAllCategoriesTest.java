
package com.aurum.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindAllCategoriesTest {

	@Mock
	private CategoryRepository categoryRepository;

	private CategoryService categoryService;

	@BeforeEach
	void setUp() {
		categoryService = new CategoryService(categoryRepository);
	}

	@Test
	void shouldReturnAllCategoriesAsResponses() {
		UUID foodId = UUID.randomUUID();
		UUID salaryId = UUID.randomUUID();

		Category food = new Category("Alimentação", CategoryType.EXPENSE);
		Category salary = new Category("Salário", CategoryType.INCOME);

		when(categoryRepository.findAll()).thenReturn(List.of(food, salary));

		List<CategoryResponse> result = categoryService.findAll();

		assertEquals(2, result.size());

		assertEquals("Alimentação", result.get(0).name());
		assertEquals(CategoryType.EXPENSE, result.get(0).type());

		assertEquals("Salário", result.get(1).name());
		assertEquals(CategoryType.INCOME, result.get(1).type());

		assertNotSame(food, result.get(0));
		assertNotSame(salary, result.get(1));

		verify(categoryRepository).findAll();
	}

	@Test
	void shouldReturnEmptyListWhenNoCategoriesExist() {
		when(categoryRepository.findAll()).thenReturn(List.of());

		List<CategoryResponse> result = categoryService.findAll();

		assertTrue(result.isEmpty());

		verify(categoryRepository).findAll();
	}
}
