<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Foodie's - Admin Orders</title>
    <link rel="stylesheet" href="/food.css">
</head>

<body>
    <div class="section-container" style="padding:40px 20px">
        <div class="small-title">ADMIN ORDERS (JSP PAGE)</div>
        <h2>Latest Orders</h2>

        <table class="admin-table">
            <thead>
                <tr>
                    <th>Order</th>
                    <th>Customer</th>
                    <th>Phone</th>
                    <th>Total</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="order" items="${orders}">
                    <tr>
                        <td>#<c:out value="${order.id}" /></td>
                        <td><c:out value="${order.customerName}" /></td>
                        <td><c:out value="${order.phone}" /></td>
                        <td>₹<c:out value="${order.totalAmount}" /></td>
                        <td><c:out value="${order.status}" /></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty orders}">
            <p class="empty">No orders yet.</p>
        </c:if>

        <p style="margin-top:20px">
            <a href="/food.html">Back to Foodie's</a>
        </p>
    </div>
</body>

</html>
