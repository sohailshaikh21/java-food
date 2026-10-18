package foodie_backend.foodie_backend.controller;

import foodie_backend.foodie_backend.dto.OrderRequest;
import foodie_backend.foodie_backend.dto.OrderResponse;
import foodie_backend.foodie_backend.model.Order;
import foodie_backend.foodie_backend.model.Person;
import foodie_backend.foodie_backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestAttribute(value = "currentUser", required = false) Person user,
            @Valid @RequestBody OrderRequest request) {
        Long userId = user == null ? null : user.getId();
        Order order = orderService.placeOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/my")
    public List<OrderResponse> getMyOrders(@RequestAttribute("currentUser") Person user) {
        return orderService.getMyOrders(user.getId()).stream()
                .map(OrderResponse::from)
                .toList();
    }
}
