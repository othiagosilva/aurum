package com.aurum.category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
	List<Category> findAllByType(CategoryType type);

	Optional<Category> findByNameIgnoreCaseAndType(String name, CategoryType type);

	boolean existsByNameIgnoreCaseAndType(String name, CategoryType type);
}
