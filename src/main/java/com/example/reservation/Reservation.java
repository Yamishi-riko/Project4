package com.example.reservation;

import java.time.LocalDateTime;

public class Reservation {
	private int id;
	private String name;
	private LocalDateTime reservationTime;
	
	public Reservation(int id, String name,LocalDateTime reservationTime) {
		this.id = id;
		this.name = name;
		this.reservationTime = reservationTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getReservationTime() {
		return reservationTime;
	}

	public void setReservationTime(LocalDateTime reservationTime) {
		this.reservationTime = reservationTime;
	}
	
	
	}
