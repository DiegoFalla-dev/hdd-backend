package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
public class Showtime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "movie_id")
	private Movie movie;

	@ManyToOne
	@JoinColumn(name = "auditorium_id")
	private Auditorium auditorium;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	public Showtime() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Movie getMovie() { return movie; }
	public void setMovie(Movie movie) { this.movie = movie; }
	public Auditorium getAuditorium() { return auditorium; }
	public void setAuditorium(Auditorium auditorium) { this.auditorium = auditorium; }
	public LocalDateTime getStartTime() { return startTime; }
	public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
	public LocalDateTime getEndTime() { return endTime; }
	public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
