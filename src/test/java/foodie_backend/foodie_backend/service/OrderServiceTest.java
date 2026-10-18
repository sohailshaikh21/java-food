package foodie_backend.foodie_backend.service;

import foodie_backend.foodie_backend.dto.OrderItemRequest;
import foodie_backend.foodie_backend.dto.OrderRequest;
import foodie_backend.foodie_backend.exception.ItemNotFoundException;
import foodie_backend.foodie_backend.model.Customer;
import foodie_backend.foodie_backend.model.MenuItem;
import foodie_backend.foodie_backend.model.Order;
import foodie_backend.foodie_backend.repository.MenuItemRepository;
import foodie_backend.foodie_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void totalIsItemsFromTheDatabasePlusDeliveryCharge() {
        long userId = createCustomer("order.total@foodies.com");
        MenuItem dosa = createMenuItem("Test Dosa", 120);
        MenuItem tea = createMenuItem("Test Tea", 30);

        Order order = orderService.placeOrder(userId, request(
                item(dosa.getId(), 2),
                item(tea.getId(), 3)));

        assertEquals(330 + OrderService.DELIVERY_CHARGE, order.getTotalAmount());
        assertEquals(OrderService.DELIVERY_CHARGE, order.getDeliveryCharge());
        assertEquals(OrderService.DEFAULT_STATUS, order.getStatus());
    }

    @Test
    void unitPricesComeFromTheDatabaseAndNotFromTheRequest() {
        long userId = createCustomer("order.prices@foodies.com");
        MenuItem pizza = createMenuItem("Test Pizza", 199);

        Order order = orderService.placeOrder(userId, request(item(pizza.getId(), 1)));

        assertEquals(199, order.getItems().get(0).getUnitPrice());
        assertEquals("Test Pizza", order.getItems().get(0).getItemName());
    }

    @Test
    void unknownMenuItemIsRejected() {
        long userId = createCustomer("order.missing@foodies.com");

        assertThrows(ItemNotFoundException.class,
                () -> orderService.placeOrder(userId, request(item(999999L, 1))));
    }

    private long createCustomer(String email) {
        Customer customer = new Customer("Test Customer", null, email);
        customer.setPasswordHash(passwordEncoder.encode("secret123"));
        return userRepository.insert(customer);
    }

    private MenuItem createMenuItem(String name, int price) {
        menuItemRepository.save(new MenuItem(name, "Test", price, "Veg", null, "item for tests", true));
        return menuItemRepository.findAll().stream()
                .filter(item -> name.equals(item.getName()))
                .findFirst()
                .orElseThrow();
    }

    private OrderItemRequest item(Long menuItemId, int quantity) {
        OrderItemRequest request = new OrderItemRequest();
        request.setMenuItemId(menuItemId);
        request.setQuantity(quantity);
        return request;
    }

    private OrderRequest request(OrderItemRequest... items) {
        OrderRequest request = new OrderRequest();
        request.setCustomerName("Test Customer");
        request.setPhone("9876543210");
        request.setAddress("1 Test Street");
        request.setPaymentMethod("Cash on Delivery");
        request.setItems(List.of(items));
        return request;
    }
}
