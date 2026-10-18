package foodie_backend.foodie_backend.controller;

import foodie_backend.foodie_backend.dto.OrderResponse;
import foodie_backend.foodie_backend.exception.AppException;
import foodie_backend.foodie_backend.model.Person;
import foodie_backend.foodie_backend.service.AuthService;
import foodie_backend.foodie_backend.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders-page")
public class AdminOrdersPageServlet extends HttpServlet {

    private AuthService authService;
    private OrderService orderService;

    @Override
    public void init() {
        WebApplicationContext context =
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        this.authService = context.getBean(AuthService.class);
        this.orderService = context.getBean(OrderService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try {
            Person person = authService.authenticate(readToken(request));
            authService.requireAdmin(person);
        } catch (AppException e) {
            response.setStatus(e.getStatus().value());
            response.getWriter().println("<h2>" + e.getMessage() + "</h2>");
            return;
        }

        List<OrderResponse> orders = orderService.getAllOrders().stream()
                .map(OrderResponse::from)
                .toList();

        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/WEB-INF/jsp/orders.jsp").forward(request, response);
    }

    private String readToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return request.getParameter("token");
    }
}
