package com.aurum.category;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aurum.category.exception.CategoryAlreadyExistsException;
import com.aurum.category.exception.CategoryNotFoundException;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Transactional
	public CategoryResponse create(CreateCategoryRequest request) {

		if (categoryRepository.existsByNameIgnoreCaseAndType(request.name(), request.type())) {
			throw new CategoryAlreadyExistsException();
		}

		Category category = new Category(request.name(), request.type());

		categoryRepository.save(category);

		return toCategoryResponse(category);
	}

	public List<CategoryResponse> findAll() {
		return categoryRepository.findAll().stream().map(this::toCategoryResponse).toList();
	}

	public CategoryResponse findById(UUID id) {
		Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
		CategoryResponse response = toCategoryResponse(category);
		return response;
	}

	public CategoryResponse toCategoryResponse(Category category) {
		CategoryResponse categoryResponse = new CategoryResponse(category.getId(), category.getName(),
				category.getType());

		return categoryResponse;
	}

}
