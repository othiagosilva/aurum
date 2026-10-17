package com.aurum.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.aurum.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "transactions")
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank
	@Size(max = 200)
	@Column(nullable = false, length = 200)
	private String description;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@NotNull
	@Positive
	@Digits(integer = 13, fraction = 2)
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionType type;

	@NotNull
	@Column(name = "transaction_date", nullable = false)
	private LocalDate transactionDate;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	protected Transaction() {

	}

	public Transaction(String description, BigDecimal amount, TransactionType type, LocalDate transactionDate,
			Category category) {
		this.description = description;
		this.amount = amount;
		this.type = type;
		this.transactionDate = transactionDate;
		this.category = category;
	}

	public UUID getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public TransactionType getType() {
		return type;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public Category getCategory() {
		return category;
	}

	@PrePersist
	public void onCreate() {
		Instant now = Instant.now();
		this.createdAt = now;
	}
}
