package com.aurum.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aurum.category.exception.CategoryAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
public class CategoryCreateTest {
	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryService categoryService;

	private CreateCategoryRequest request;

	@BeforeEach
	void setUp() {
		request = new CreateCategoryRequest("Alimentação", CategoryType.EXPENSE);
	}

	@Test
	void shouldCreateCategory() {
		when(categoryRepository.existsByNameIgnoreCaseAndType(request.name(), request.type())).thenReturn(false);
		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CategoryResponse result = categoryService.create(request);

		assertNotNull(result);
		assertEquals("Alimentação", result.name());
		assertEquals(CategoryType.EXPENSE, result.type());

		verify(categoryRepository).existsByNameIgnoreCaseAndType(request.name(), request.type());
		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	void shouldRejectDuplicateCategory() {
		when(categoryRepository.existsByNameIgnoreCaseAndType(request.name(), request.type())).thenReturn(true);

		assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.create(request));

		verify(categoryRepository).existsByNameIgnoreCaseAndType(request.name(), request.type());
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	void shouldCheckDuplicateUsingProvidedNameAndType() {
		when(categoryRepository.existsByNameIgnoreCaseAndType("Salário", CategoryType.INCOME)).thenReturn(false);

		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CreateCategoryRequest incomeRequest = new CreateCategoryRequest("Salário", CategoryType.INCOME);

		categoryService.create(incomeRequest);

		verify(categoryRepository).existsByNameIgnoreCaseAndType("Salário", CategoryType.INCOME);
	}

	@Test
	void shouldNotSaveWhenDuplicateCheckFindsExistingCategory() {
		when(categoryRepository.existsByNameIgnoreCaseAndType(request.name(), request.type())).thenReturn(true);

		assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.create(request));

		verify(categoryRepository, never()).save(any());
	}
}
