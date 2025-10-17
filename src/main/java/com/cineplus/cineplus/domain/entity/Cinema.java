package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cinemas")
public class Cinema {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String address;

	public Cinema() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
}
