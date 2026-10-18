package foodie_backend.foodie_backend.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {

    private Long id;
    private String customerName;
    private String phone;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private int guests;

    public Reservation() {
    }

    public Reservation(String customerName, String phone, LocalDate reservationDate,
                       LocalTime reservationTime, int guests) {
        this.customerName = customerName;
        this.phone = phone;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.guests = guests;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }
}
