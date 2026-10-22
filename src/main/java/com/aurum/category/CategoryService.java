package com.aurum.category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aurum.category.exception.CategoryAlreadyExistsException;
import com.aurum.category.exception.CategoryIsNotActiveException;
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
		return toCategoryResponse(category);
	}

	@Transactional
	public CategoryResponse update(UUID id, CreateCategoryRequest request) {
		Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

		if (category.isActive()) {
			Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCaseAndType(request.name(),
					request.type());

			if (existingCategory.isPresent() && !existingCategory.get().getId().equals(id)) {
				throw new CategoryAlreadyExistsException();
			}

			category.update(request.name(), request.type());

			return toCategoryResponse(category);
		} else {
			throw new CategoryIsNotActiveException();
		}

	}

	@Transactional
	public CategoryResponse delete(UUID id) {
		Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

		category.deactivate();

		return toCategoryResponse(category);
	}

	private CategoryResponse toCategoryResponse(Category category) {
		CategoryResponse categoryResponse = new CategoryResponse(category.getId(), category.getName(),
				category.getType());

		return categoryResponse;
	}

}
