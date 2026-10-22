package com.aurum.category;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, length = 100)
	private String name;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private boolean active = true;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CategoryType type;

	protected Category() {
	}

	public Category(String name, CategoryType type) {
		this.name = name;
		this.type = type;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public CategoryType getType() {
		return type;
	}

	@PrePersist
	public void onCreate() {
		Instant now = Instant.now();
		this.createdAt = now;
	}

	public void update(String name, CategoryType type) {
		this.name = name;
		this.type = type;
	}

	public void deactivate() {
		this.active = false;
	}

	public boolean isActive() {
		return this.active;
	}
}