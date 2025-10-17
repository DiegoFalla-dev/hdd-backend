package com.cineplus.cineplus.domain.dto;

public class MovieDTO {
    private Long id;
    private String title;
    private String description;
    private String genre;

    public MovieDTO() {}

    public MovieDTO(Long id, String title, String description, String genre) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}

