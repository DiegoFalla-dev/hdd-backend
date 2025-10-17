package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String description;

	private String genre;

	private Integer durationMinutes;

	private LocalDate releaseDate;

	private String posterUrl;

	public Movie() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getGenre() { return genre; }
	public void setGenre(String genre) { this.genre = genre; }
	public Integer getDurationMinutes() { return durationMinutes; }
	public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
	public LocalDate getReleaseDate() { return releaseDate; }
	public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
	public String getPosterUrl() { return posterUrl; }
	public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
}
