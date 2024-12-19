import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Flight {
    private String flightNumber;
    private String origin;
    private String destination;
    private Date departureDate;
    private double ticketPrice;
    private List<String> economySeats;
    private List<String> businessSeats;
    private List<String> firstClassSeats;
    private List<Reservation> reservations;
    private boolean isFull;

    public Flight(String flightNumber, String origin, String destination, Date departureDate, int economySeatsCount, int businessSeatsCount, int firstClassSeatsCount, double ticketPrice) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.ticketPrice = ticketPrice;
        this.economySeats = initializeSeats(economySeatsCount);
        this.businessSeats = initializeSeats(businessSeatsCount);
        this.firstClassSeats = initializeSeats(firstClassSeatsCount);
        this.isFull = false;
    }

    private List<String> initializeSeats(int seatCount) {
        List<String> seats = new ArrayList<>();
        for (int i = 1; i <= seatCount; i++) {
            seats.add(String.valueOf(i));
        }
        return seats;
    }

    public boolean hasAvailableSeats(String seatClass) {
        switch (seatClass.toLowerCase()) {
            case "economy":
                return !economySeats.isEmpty();
            case "business":
                return !businessSeats.isEmpty();
            case "firstclass":
                return !firstClassSeats.isEmpty();
            default:
                return false;
        }
    }

    public boolean bookSeat(String seatClass, String seatNumber) {
        switch (seatClass.toLowerCase()) {
            case "economy":
                return bookSeat(economySeats, seatNumber);
            case "business":
                return bookSeat(businessSeats, seatNumber);
            case "firstclass":
                return bookSeat(firstClassSeats, seatNumber);
            default:
                return false;
        }
    }

    private boolean bookSeat(List<String> seats, String seatNumber) {
        if (seats.contains(seatNumber)) {
            seats.remove(seatNumber);
            if (seats.isEmpty()) {
                isFull = true;
            }
            return true;
        }
        return false;
    }

    public List<String> getAvailableSeats(String seatClass) {
        switch (seatClass.toLowerCase()) {
            case "economy":
                return new ArrayList<>(economySeats);
            case "business":
                return new ArrayList<>(businessSeats);
            case "firstclass":
                return new ArrayList<>(firstClassSeats);
            default:
                return new ArrayList<>();
        }
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setIsFull(boolean isFull) {
        this.isFull = isFull;
    }

    public List<Reservation> getReservations() {
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        return reservations;
    }

    public void writeToFile(BufferedWriter writer) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(departureDate);
        writer.write(flightNumber + "," + origin + "," + destination + "," + formattedDate + "," +
        economySeats.size() + "," + businessSeats.size() + "," + firstClassSeats.size() + "," + ticketPrice + "\n");
    }

    public static Flight parseFlight(String line) throws ParseException {
        String[] parts = line.split(",");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date departureDate = dateFormat.parse(parts[3]);
        double ticketPrice = Double.parseDouble(parts[7]);
        return new Flight(parts[0], parts[1], parts[2], departureDate,
                Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]), ticketPrice);
    }

    public void addReservation(Reservation reservation) {
        getReservations().add(reservation);
    }
}

class Passenger {
    private String name;
    private String email;
    private String phoneNumber;
    private String specialRequest;

    public Passenger(String name, String email, String phoneNumber, String specialRequest) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.specialRequest = specialRequest;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }
}

class Reservation {
    private Passenger passenger;
    private Flight flight;
    private String seatClass;
    private String seatNumber;
    private boolean cancelled;

    public Reservation(Passenger passenger, Flight flight, String seatClass, String seatNumber) {
        this.passenger = passenger;
        this.flight = flight;
        this.seatClass = seatClass;
        this.seatNumber = seatNumber;
        this.cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
        this.flight.getAvailableSeats(seatClass).add(seatNumber);
        this.flight.setIsFull(false);
    }

    public double calculateRefund() {
        double refundPercentage;

        switch (seatClass.toLowerCase()) {
            case "economy":
                refundPercentage = 0.1; 
                break;
            case "business":
                refundPercentage = 0.15; 
                break;
            case "firstclass":
                refundPercentage = 0.2; 
                break;
            default:
                refundPercentage = 0.0; 
                break;
        }
        double refundAmount = refundPercentage * flight.getTicketPrice();
        
        return refundAmount;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public String getSeatNumber() {
        return seatNumber;
    }
}

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

public class FlightReservationSystem {
    private static List<Flight> flights = new ArrayList<>();
    private static final String FLIGHTS_FILE = "flights.csv";
    private static final String USERS_FILE = "users.csv";
    private static User currentUser;

    public static void main(String[] args) {
        loadFlights(); 
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (currentUser == null) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();
                if (login(username, password)) {
                    System.out.println("Login successful!");
                } else {
                    System.out.println("Invalid username or password. Please try again.");
                    continue;
                }
            }

            System.out.println("\nWelcome to Flight Reservation System");
            System.out.println("1. Search Flights");
            System.out.println("2. Make Reservation");
            System.out.println("3. Display Reservations");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Logout");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    searchFlights(scanner);
                    break;
                case 2:
                    makeReservation(scanner);
                    break;
                case 3:
                    displayReservations();
                    break;
                case 4:
                    cancelReservation(scanner);
                    break;
                case 5:
                    currentUser = null;
                    System.out.println("Logout successful!");
                    break;
                case 6:
                    saveFlights(); 
                    System.out.println("Thank you for using Flight Reservation System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void loadFlights() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FLIGHTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Flight flight = Flight.parseFlight(line);
                flights.add(flight);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error loading flights data: " + e.getMessage());
        }
    }

    private static void saveFlights() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FLIGHTS_FILE))) {
            for (Flight flight : flights) {
                flight.writeToFile(writer);
            }
        } catch (IOException e) {
            System.err.println("Error saving flights data: " + e.getMessage());
        }
    }

    private static boolean login(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); 
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    currentUser = new User(username, password);
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user database.: " + e.getMessage());
        }
        return false;
    }

    private static void searchFlights(Scanner scanner) {
        for (Flight flight : flights) {
            System.out.println(flight.getFlightNumber() + " - " + flight.getOrigin() + " to " + flight.getDestination() + " Departure: " + flight.getDepartureDate());
        }
    }

private static void makeReservation(Scanner scanner) {

    System.out.println("Available Flights:");
    for (Flight flight : flights) {
        System.out.println(flight.getFlightNumber() + " - " + flight.getOrigin() + " to " + flight.getDestination() + " Departure: " + flight.getDepartureDate());
    }

    System.out.print("Enter the flight number you want to book: ");
    String flightNumber = scanner.nextLine();

    Flight selectedFlight = null;
    for (Flight flight : flights) {
        if (flight.getFlightNumber().equalsIgnoreCase(flightNumber)) {
            selectedFlight = flight;
            break;
        }
    }

    if (selectedFlight == null) {
        System.out.println("Flight not found. Please enter a valid flight number.");
        return;
    }

    System.out.print("Enter passenger name: ");
    String name = scanner.nextLine();
    System.out.print("Enter passenger email: ");
    String email = scanner.nextLine();
    System.out.print("Enter passenger phone number: ");
    String phoneNumber = scanner.nextLine();
    System.out.print("Enter any special request (optional): ");
    String specialRequest = scanner.nextLine();

    Passenger passenger = new Passenger(name, email, phoneNumber, specialRequest);

    System.out.print("Enter seat class (Economy/Business/FirstClass): ");
    String seatClass = scanner.nextLine().toLowerCase();

    if (!selectedFlight.hasAvailableSeats(seatClass)) {
        System.out.println("Sorry, no available seats in " + seatClass + " class.");
        return;
    }

    List<String> availableSeats = selectedFlight.getAvailableSeats(seatClass);

    System.out.println("Available seats in " + seatClass + " class:");
    for (String seat : availableSeats) {
        System.out.print(seat + " ");
    }
    System.out.println();

    System.out.print("Enter the seat number you want to book: ");
    String seatNumber = scanner.nextLine();

    if (!selectedFlight.bookSeat(seatClass, seatNumber)) {
        System.out.println("Seat " + seatNumber + " is not available.");
        return;
    }

    Reservation reservation = new Reservation(passenger, selectedFlight, seatClass, seatNumber);
    selectedFlight.addReservation(reservation);
    System.out.println("Reservation successful! Your reservation details:");
    System.out.println("Flight: " + selectedFlight.getFlightNumber() + " - " + selectedFlight.getOrigin() + " to " + selectedFlight.getDestination());
    System.out.println("Departure Date: " + selectedFlight.getDepartureDate());
    System.out.println("Passenger: " + passenger.getName());
    System.out.println("Seat Class: " + seatClass);
    System.out.println("Seat Number: " + seatNumber);
}

private static void displayReservations() {
    for (Flight flight : flights) {
        System.out.println("Flight: " + flight.getFlightNumber() + " - " + flight.getOrigin() + " to " + flight.getDestination());

        List<Reservation> reservations = flight.getReservations();
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("No reservations for this flight.");
        } else {
            for (Reservation reservation : reservations) {
                Passenger passenger = reservation.getPassenger();
                System.out.println("Passenger: " + passenger.getName());
                System.out.println("Seat Class: " + reservation.getSeatClass());
                System.out.println("Seat Number: " + reservation.getSeatNumber());
                System.out.println("Ticket Price: " + flight.getTicketPrice()); 
                double refundAmount = reservation.calculateRefund();
                System.out.println("Refunded Amount: " + refundAmount); 
                System.out.println("Cancelled: " + (reservation.isCancelled() ? "Yes" : "No"));
                System.out.println(); 
            }
        }
        System.out.println(); 
    }
}
private static void cancelReservation(Scanner scanner) {
    System.out.print("Enter passenger name: ");
    String passengerName = scanner.nextLine();

    Reservation reservationToCancel = null;
    for (Flight flight : flights) {
        for (Reservation reservation : flight.getReservations()) {
            if (reservation.getPassenger().getName().equalsIgnoreCase(passengerName)) {
                reservationToCancel = reservation;
                break;
            }
        }
        if (reservationToCancel != null) {
            break;
        }
    }

    if (reservationToCancel == null) {
        System.out.println("No reservation found for passenger: " + passengerName);
        return;
    }

    reservationToCancel.cancel();
    System.out.println("Reservation cancelled for passenger: " + passengerName);
}

}