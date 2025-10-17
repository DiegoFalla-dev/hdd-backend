package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "showtime_id")
	private Showtime showtime;

	private Integer seats;

	private Double totalPrice;

	public Booking() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }
	public Showtime getShowtime() { return showtime; }
	public void setShowtime(Showtime showtime) { this.showtime = showtime; }
	public Integer getSeats() { return seats; }
	public void setSeats(Integer seats) { this.seats = seats; }
	public Double getTotalPrice() { return totalPrice; }
	public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}
