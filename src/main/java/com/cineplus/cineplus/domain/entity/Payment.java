package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String method;

	private Double amount;

	public Payment() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getMethod() { return method; }
	public void setMethod(String method) { this.method = method; }
	public Double getAmount() { return amount; }
	public void setAmount(Double amount) { this.amount = amount; }
}
