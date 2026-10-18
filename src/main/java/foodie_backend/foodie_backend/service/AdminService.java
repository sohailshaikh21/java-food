package foodie_backend.foodie_backend.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminService {

    private final OrderService orderService;
    private final ReservationService reservationService;

    public AdminService(OrderService orderService, ReservationService reservationService) {
        this.orderService = orderService;
        this.reservationService = reservationService;
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("orders", orderService.countOrders());
        stats.put("reservations", reservationService.countReservations());
        return stats;
    }
}
