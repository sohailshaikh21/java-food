package foodie_backend.foodie_backend.service;

import foodie_backend.foodie_backend.dto.ReservationRequest;
import foodie_backend.foodie_backend.exception.InvalidOrderException;
import foodie_backend.foodie_backend.model.Reservation;
import foodie_backend.foodie_backend.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation create(ReservationRequest request) {
        if (request.getReservationDate().isBefore(LocalDate.now())) {
            throw new InvalidOrderException("Reservation date cannot be in the past");
        }

        Reservation reservation = new Reservation(request.getCustomerName(), request.getPhone(),
                request.getReservationDate(), request.getReservationTime(), request.getGuests());
        reservationRepository.save(reservation);
        return reservation;
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public int countReservations() {
        return reservationRepository.count();
    }
}
