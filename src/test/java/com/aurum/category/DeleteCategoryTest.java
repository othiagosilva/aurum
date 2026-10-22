package com.aurum.category;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
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
class DeleteCategoryTest {

	@Mock
	private CategoryRepository categoryRepository;

	private CategoryService categoryService;

	@BeforeEach
	void setUp() {
		categoryService = new CategoryService(categoryRepository);
	}

	@Test
	void shouldDeactivateCategorySuccessfully() {
		UUID categoryId = UUID.randomUUID();

		Category category = new Category("Alimentação", CategoryType.EXPENSE);
		ReflectionTestUtils.setField(category, "id", categoryId);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		categoryService.delete(categoryId);

		assertFalse(category.isActive());

		verify(categoryRepository).findById(categoryId);
		verify(categoryRepository, never()).delete(category);
		verify(categoryRepository, never()).deleteById(categoryId);
	}

	@Test
	void shouldThrowWhenCategoryDoesNotExist() {
		UUID categoryId = UUID.randomUUID();

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> categoryService.delete(categoryId));

		verify(categoryRepository).findById(categoryId);
		verify(categoryRepository, never()).deleteById(categoryId);
	}
}