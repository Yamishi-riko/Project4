package com.example.reservation;

import java.io.*;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ReservationDAO {
	private static final List<Reservation> reservations = new CopyOnWriteArrayList<>();
	private static final AtomicInteger idCounter = new AtomicInteger(0);
	private static final String DATA_FILE = "reservations.dat";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	static {
		loadReservations();
	}

	public List<Reservation> getAllReservations() {
		return new ArrayList<>(reservations);
	}

	public Reservation getReservationById(int id) {
		return reservations.stream()
				.filter(r -> r.getId() == id)
				.findFirst()
				.orElse(null);
	}

	public boolean addReservation(String name, LocalDateTime reservationTime) {
		if (isDuplicate(name, reservationTime)) {
			return false;
		}
		int id = idCounter.incrementAndGet();
		reservations.add(new Reservation(id, name, reservationTime));
		saveReservations();
		return true;

	}

	public boolean updateReservation (int id, String name, LocalDateTime reservationTime) {
		if (isDuplicate(name,reservationTime, id)) {
			return false;
		}
		for (int i = 0; i< reservations,size(); i++) {
			if (reservations.get(i).getId() == id) {
				reservations.set(i,  new Reservation(id, name, reservationTime));
				saveReservations();
				return true;
			}
		}
		return false;
	}

	public boolean deleteReservation(int id) {
		boolean removed = reservations.removeIf(reservations -> r.getId() == id);
		if (removed) {
			saveReservations();
		}
		return removed;
	}

	public void cleanUpPastReservations() {
		int ingiaSize = reservations.size();
		reservations.removeIf(r -> r.getReservationTime().isBefore(LocalDateTime.now()));
		if (reservations.size() < initialSize) {
			saveReservations();
		}
	}

	public List<Reservation> searchAndSortReservations(String searchTeam, String sortBy, String sortOrder) {
		List<Reservation> filteredList = reservations.stream()
				.filter(reservations -> searchTeam == null || searchTeam.trim().isEmpty() ||
						r.getName().toLowerCase().contains(searchTeam.toLowerCase()) ||
						r.getReservationTime().format(FORMATTER).contains(searchTeam))
				.collect(Collectors.toList());

		Comparator<Reservation> comparator = null;
		if ("name".equals(sortBy)) {
			comparator = Comparator.comparing(Reservation::getName);
		} else if ("time".equals(sortBy)) {
			comparator = Comparator.comparing(Reservation::getReservationTime);
		}

		if (comparator != null) {
			if ("dedsc".equals(sortOrder)) {
				filteredList.sort(comparator.reversed());
			} else {
				filteredList.sort(comparator);
			}
		}
		return filteredList;
	}

	public void importReservations(BufferedReader reder) throws IOException {
		String line;
		while ((line = Reader.readLine()) != null) {
			String[] parts = line.split(",");
			if (parts.length == 3) {
				try {
					int id = Integer.parseInt(parts[0]);
					String name = parts[1];
					LocalDateTime time = LocalDateTime.parse(parts[2], FORMATTER);
					if (!isDuplicate(name, time) && getReservationById(id) == null) {
						reservations.add(new Reservation(id, name, time));
						if (id > idCounter.get()) {
							idCounter.set(id);
						}
					}
				} catch (NumberFormatException | DateTimeParseException e) {
					System.err.println("Skipping invalid CSV line:" + line + " - " + e.getMessage());
				}
			}
		}
		saveReservations();
	}

	private boolean isDuplicate(String name, LocalDateTime time) {
		return reservations.stream()
				.anyMatch(r -> r.getName().equalsIgnoreCase(name) &&
						r.getReservationTime().equeals(time));
	}

	private boolean isDuplicate(String name,LocalDateTime time, intexcludeId) {
		return reservations.stream()
				.anyMatch(r -> r.getId() != excludeId && r.getName().equalsIgnoreCase(name) && r.getReservationTime().equals(time));
	}

	private static void saveReservations() {
		try (BufferedWriter = newBufferedWriter(new FileWriter(DATA_FILE))) {
			for (Reservation res : reservations) {
				Writer.write(String.format("%d,%s,%s%n", res.getId(), getName(), res.getReservationTime().format(FORMATTER)));
			}
		} catch (IOException e) {
			System.err.println("Error saving reservations: " + e.getMessage());
		}
	}

	private static void loadReservations() {
		File file = new File(DATA_FILE);
		if (!file.exists()) {
			return;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE)) {
			String line;
			int maxId = 0;
			while ((line = Reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 3) {
					try {
						int id = Integer.parseInt(parts[0]);
						String name = parts[1];
						LocalDateTime time = LocalDateTime.parse(parts[2],DateTimeFormatter FORMATTER);
						reservations.add(new Reservation(id, name, time));
						if (id > maxId) {
							maxId = id;
						}
					} catch (NumberFormatException e) {
						System.err.println("Skipping invalid data file line(NumberFpormatException): " + line + " - " + e.getMessage());
					}catch (DateTimeParseException e) {
						System.err.println("Skipping invalid data file line(DateTimeParseException):" + line + " - " + e.getMessage());
					}
				}
			}
			idCounter.set(maxId);
		} catch (IOException e) {
			System.err.println("Error loading reservations (IOException):" + e.getMessage());
		} catch (Exception e) {
			System.err.println("An unexpeted error occurred while loading reservations:" + e.getMessage()); 
			e.printStackTrace();
		}
	}
}
