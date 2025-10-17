package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.MovieDTO;
import com.cineplus.cineplus.domain.entity.Movie;
import com.cineplus.cineplus.domain.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/movies")
public class MovieController {

	@Autowired
	private MovieRepository movieRepository;

	@GetMapping
	public List<MovieDTO> list() {
		List<Movie> movies = movieRepository.findAll();
		return movies.stream().map(m -> new MovieDTO(m.getId(), m.getTitle(), m.getDescription(), m.getGenre())).collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public MovieDTO get(@PathVariable Long id) {
		Movie m = movieRepository.findById(id).orElseThrow();
		return new MovieDTO(m.getId(), m.getTitle(), m.getDescription(), m.getGenre());
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> create(@RequestBody MovieDTO dto) {
		Movie m = new Movie();
		m.setTitle(dto.getTitle());
		m.setDescription(dto.getDescription());
		m.setGenre(dto.getGenre());
		m = movieRepository.save(m);
		return ResponseEntity.created(URI.create("/api/public/movies/" + m.getId())).body(new MovieDTO(m.getId(), m.getTitle(), m.getDescription(), m.getGenre()));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public MovieDTO update(@PathVariable Long id, @RequestBody MovieDTO dto) {
		Movie m = movieRepository.findById(id).orElseThrow();
		m.setTitle(dto.getTitle());
		m.setDescription(dto.getDescription());
		m.setGenre(dto.getGenre());
		m = movieRepository.save(m);
		return new MovieDTO(m.getId(), m.getTitle(), m.getDescription(), m.getGenre());
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		movieRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
