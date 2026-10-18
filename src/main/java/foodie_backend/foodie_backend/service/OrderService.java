package foodie_backend.foodie_backend.service;

import foodie_backend.foodie_backend.dto.OrderItemRequest;
import foodie_backend.foodie_backend.dto.OrderRequest;
import foodie_backend.foodie_backend.exception.InvalidOrderException;
import foodie_backend.foodie_backend.exception.ItemNotFoundException;
import foodie_backend.foodie_backend.model.MenuItem;
import foodie_backend.foodie_backend.model.Order;
import foodie_backend.foodie_backend.model.OrderItem;
import foodie_backend.foodie_backend.repository.MenuItemRepository;
import foodie_backend.foodie_backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    public static final int DELIVERY_CHARGE = 40;
    public static final String DEFAULT_STATUS = "Order Placed";

    public static final Set<String> ALLOWED_STATUSES = Set.of(
            "Order Placed",
            "Order Confirmed",
            "Preparing",
            "Ready",
            "Out for Delivery",
            "Delivered");

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public Order placeOrder(Long userId, OrderRequest request) {
        List<OrderItem> items = new ArrayList<>();
        int subtotal = 0;

        for (OrderItemRequest requested : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(requested.getMenuItemId())
                    .orElseThrow(() -> new ItemNotFoundException("Menu item not found: " + requested.getMenuItemId()));

            if (!menuItem.isAvailable()) {
                throw new InvalidOrderException(menuItem.getName() + " is currently not available");
            }

            OrderItem item = new OrderItem(menuItem.getId(), menuItem.getName(),
                    menuItem.getPrice(), requested.getQuantity());
            items.add(item);
            subtotal += item.getLineTotal();
        }

        Order order = new Order(request.getCustomerName(), request.getPhone(),
                request.getAddress(), request.getPaymentMethod());
        order.setUserId(userId);
        order.setItems(items);
        order.setDeliveryCharge(DELIVERY_CHARGE);
        order.setTotalAmount(subtotal + DELIVERY_CHARGE);
        order.setStatus(DEFAULT_STATUS);

        long orderId = orderRepository.save(order);
        items.forEach(item -> item.setOrderId(orderId));
        orderRepository.saveItems(orderId, items);

        return order;
    }

    public List<Order> getMyOrders(Long userId) {
        return withItems(orderRepository.findByUserId(userId));
    }

    public List<Order> getAllOrders() {
        return withItems(orderRepository.findAll());
    }

    private List<Order> withItems(List<Order> orders) {
        orders.forEach(order -> order.setItems(orderRepository.findItems(order.getId())));
        return orders;
    }

    public void updateStatus(Long orderId, String status) {
        if (status == null || !ALLOWED_STATUSES.contains(status)) {
            throw new InvalidOrderException("Unknown order status: " + status);
        }
        orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order not found: " + orderId));
        orderRepository.updateStatus(orderId, status);
    }

    public int countOrders() {
        return orderRepository.count();
    }
}
