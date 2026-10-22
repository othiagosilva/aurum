package com.aurum.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.aurum.category.exception.CategoryNotFoundException;

@ExtendWith(MockitoExtension.class)
public class FindByIdTest {
	@Mock
	private CategoryRepository categoryRepository;

	private CategoryService categoryService;

	@BeforeEach
	void setUp() {
		categoryService = new CategoryService(categoryRepository);
	}

	@Test
	void shouldFindId() {
		UUID foodId = UUID.randomUUID();
		Category food = new Category("Alimentação", CategoryType.EXPENSE);

		ReflectionTestUtils.setField(food, "id", foodId);

		when(categoryRepository.findById(foodId)).thenReturn(Optional.of(food));

		CategoryResponse result = categoryService.findById(foodId);

		assertEquals("Alimentação", result.name());
		assertEquals(foodId, result.id());

		verify(categoryRepository).findById(foodId);
	}

	@Test
	void shouldNotFindCategory() {
		UUID foodId = UUID.randomUUID();

		when(categoryRepository.findById(foodId)).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(foodId));

		verify(categoryRepository).findById(foodId);
	}
}
