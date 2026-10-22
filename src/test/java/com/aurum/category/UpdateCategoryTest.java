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

import com.aurum.category.exception.CategoryAlreadyExistsException;
import com.aurum.category.exception.CategoryNotFoundException;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryTest {

	@Mock
	private CategoryRepository categoryRepository;

	private CategoryService categoryService;

	@BeforeEach
	void setUp() {
		categoryService = new CategoryService(categoryRepository);
	}

	@Test
	void shouldUpdateCategorySuccessfully() {
		UUID categoryId = UUID.randomUUID();

		Category category = new Category("Alimentação", CategoryType.EXPENSE);
		ReflectionTestUtils.setField(category, "id", categoryId);

		CreateCategoryRequest request = new CreateCategoryRequest("Supermercado", CategoryType.EXPENSE);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		when(categoryRepository.findByNameIgnoreCaseAndType(request.name(), request.type()))
				.thenReturn(Optional.empty());

		CategoryResponse result = categoryService.update(categoryId, request);

		assertEquals(categoryId, result.id());
		assertEquals("Supermercado", result.name());
		assertEquals(CategoryType.EXPENSE, result.type());

		verify(categoryRepository).findById(categoryId);
		verify(categoryRepository).findByNameIgnoreCaseAndType(request.name(), request.type());
	}

	@Test
	void shouldThrowWhenCategoryDoesNotExist() {
		UUID categoryId = UUID.randomUUID();

		CreateCategoryRequest request = new CreateCategoryRequest("Supermercado", CategoryType.EXPENSE);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> categoryService.update(categoryId, request));

		verify(categoryRepository).findById(categoryId);
		verifyNoInteractionsAfterFindById();
	}

	@Test
	void shouldThrowWhenNameAndTypeBelongToAnotherCategory() {
		UUID categoryId = UUID.randomUUID();
		UUID anotherCategoryId = UUID.randomUUID();

		Category category = new Category("Alimentação", CategoryType.EXPENSE);
		ReflectionTestUtils.setField(category, "id", categoryId);

		Category anotherCategory = new Category("Supermercado", CategoryType.EXPENSE);
		ReflectionTestUtils.setField(anotherCategory, "id", anotherCategoryId);

		CreateCategoryRequest request = new CreateCategoryRequest("Supermercado", CategoryType.EXPENSE);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		when(categoryRepository.findByNameIgnoreCaseAndType(request.name(), request.type()))
				.thenReturn(Optional.of(anotherCategory));

		assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.update(categoryId, request));

		assertEquals("Alimentação", category.getName());
		verify(categoryRepository).findById(categoryId);
		verify(categoryRepository).findByNameIgnoreCaseAndType(request.name(), request.type());
	}

	@Test
	void shouldAllowSameNameAndTypeForSameCategory() {
		UUID categoryId = UUID.randomUUID();

		Category category = new Category("Alimentação", CategoryType.EXPENSE);
		ReflectionTestUtils.setField(category, "id", categoryId);

		CreateCategoryRequest request = new CreateCategoryRequest("Alimentação", CategoryType.EXPENSE);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		when(categoryRepository.findByNameIgnoreCaseAndType(request.name(), request.type()))
				.thenReturn(Optional.of(category));

		CategoryResponse result = categoryService.update(categoryId, request);

		assertEquals(categoryId, result.id());
		assertEquals("Alimentação", result.name());
		assertEquals(CategoryType.EXPENSE, result.type());
	}

	@Test
	void shouldAllowSameNameWithDifferentType() {
		UUID categoryId = UUID.randomUUID();

		Category category = new Category("Salário", CategoryType.INCOME);
		ReflectionTestUtils.setField(category, "id", categoryId);

		CreateCategoryRequest request = new CreateCategoryRequest("Salário", CategoryType.EXPENSE);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		when(categoryRepository.findByNameIgnoreCaseAndType(request.name(), request.type()))
				.thenReturn(Optional.empty());

		CategoryResponse result = categoryService.update(categoryId, request);

		assertEquals(categoryId, result.id());
		assertEquals("Salário", result.name());
		assertEquals(CategoryType.EXPENSE, result.type());
	}

	private void verifyNoInteractionsAfterFindById() {
		org.mockito.Mockito.verifyNoMoreInteractions(categoryRepository);
	}
}