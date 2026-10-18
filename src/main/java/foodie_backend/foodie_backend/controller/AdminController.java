package foodie_backend.foodie_backend.controller;

import foodie_backend.foodie_backend.dto.OrderResponse;
import foodie_backend.foodie_backend.dto.StatusUpdateRequest;
import foodie_backend.foodie_backend.model.Reservation;
import foodie_backend.foodie_backend.service.AdminService;
import foodie_backend.foodie_backend.service.OrderService;
import foodie_backend.foodie_backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final OrderService orderService;
    private final ReservationService reservationService;
    private final AdminService adminService;

    public AdminController(OrderService orderService,
                           ReservationService reservationService,
                           AdminService adminService) {
        this.orderService = orderService;
        this.reservationService = reservationService;
        this.adminService = adminService;
    }

    @GetMapping("/orders")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reservationService.getAll();
    }

    @GetMapping("/stats")
    public Map<String, Integer> getStats() {
        return adminService.getStats();
    }

    @PutMapping("/orders/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable Long id,
                                            @Valid @RequestBody StatusUpdateRequest request) {
        orderService.updateStatus(id, request.getStatus());
        return Map.of("id", id, "status", request.getStatus());
    }
}
