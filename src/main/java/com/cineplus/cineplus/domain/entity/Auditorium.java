package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "auditoriums")
public class Auditorium {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Integer capacity;

	private String location;

	public Auditorium() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public Integer getCapacity() { return capacity; }
	public void setCapacity(Integer capacity) { this.capacity = capacity; }
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
}
