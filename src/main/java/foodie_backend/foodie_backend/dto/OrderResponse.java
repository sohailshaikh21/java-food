package foodie_backend.foodie_backend.dto;

import foodie_backend.foodie_backend.model.Order;
import foodie_backend.foodie_backend.model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private String customerName;
    private String phone;
    private String address;
    private String paymentMethod;
    private int deliveryCharge;
    private int totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private List<OrderItem> items;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.id = order.getId();
        response.customerName = order.getCustomerName();
        response.phone = order.getPhone();
        response.address = order.getAddress();
        response.paymentMethod = order.getPaymentMethod();
        response.deliveryCharge = order.getDeliveryCharge();
        response.totalAmount = order.getTotalAmount();
        response.status = order.getStatus();
        response.orderDate = order.getOrderDate();
        response.items = order.getItems();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int getDeliveryCharge() {
        return deliveryCharge;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
