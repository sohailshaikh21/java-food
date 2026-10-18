# Foodie's

A small restaurant web application built with Spring Boot. It handles online food ordering and
table reservations through a REST API, and the front page is served by the same application.

This is a college mini project for the **Full Stack Java Programming** course, so the code is kept
simple: plain Spring MVC, Spring JDBC and plain HTML/CSS/JS. No Spring Security and no JPA.

## Features

- Browse the menu by category (Pizza, Burgers, Pasta, Main Course, Starters, Desserts, Beverages)
- Live menu search and a "Veg only" filter
- Add items to the cart and place an order with delivery details
- Prices are always calculated on the server, the browser never sends a price or a total
- Reserve a table with date, time and number of guests
- Signup / login, and "My Orders" shows only the orders of the logged in user
- Track the latest order using the status the server sends
- Admin dashboard: all orders (with a status dropdown) and all reservations

## Tech Stack

- Java 17
- Spring Boot 4.1.1 (Spring Web MVC, Spring JDBC, Bean Validation, AspectJ)
- MySQL 8
- Maven (a wrapper is included, so you do not need Maven installed)
- HTML, CSS and vanilla JavaScript for the front page
- React 18 from a CDN for one extra page

## Project Structure

```
foodie-backend/
├── docs/                               GitHub Pages static deployment (index.html, food.css, food.js)
├── Dockerfile                          Multi-stage Dockerfile for cloud container hosting
├── render.yaml                         Render Blueprint for 1-click cloud deployment
├── system.properties                   Java 17 runtime declaration for cloud buildpacks
├── .github/workflows/build.yml         Automated GitHub Actions CI build & test
├── src/main/java/foodie_backend/foodie_backend/
│   ├── FoodieBackendApplication.java   starts the app
│   ├── model/                          Person (abstract), Customer, Admin, Order, OrderItem,
│   │                                   Reservation, MenuItem
│   ├── repository/                     all SQL lives here (JdbcTemplate)
│   ├── service/                        business rules (prices, status, login tokens)
│   ├── controller/                     thin HTTP layer
│   ├── dto/                            request and response objects with validation
│   ├── exception/                      AppException + subclasses and GlobalExceptionHandler
│   ├── config/                         seeders, interceptor, beans, CORS config
│   ├── aspect/                         LoggingAspect (AOP)
│   └── report/                         AdminReportJdbc (plain JDBC, Module III)
├── src/main/resources/
│   ├── schema.sql                      creates tables safely (CREATE TABLE IF NOT EXISTS)
│   ├── application.properties          configured with dynamic PORT and cloud DB env vars
│   ├── application-h2.properties       in-memory profile for instant cloud demo without MySQL
│   └── static/                         food.html, index.html, food.css, food.js, react/index.html
├── src/main/webapp/WEB-INF/jsp/orders.jsp   admin orders page (JSP + JSTL)
├── src/test/                           tests that run on an in-memory H2 database
└── pom.xml
```

## Getting Started

### Prerequisites

- JDK 17 or newer
- MySQL 8 running on `localhost:3306`
- No need to install Maven, the project ships with the `mvnw` wrapper

### 1. Create the database

Open a MySQL console and create the database:

```sql
CREATE DATABASE foodie_db;
```

> **WARNING:** `src/main/resources/schema.sql` starts with `DROP TABLE IF EXISTS`, so running it
> **deletes all existing orders and reservations**. If you already had the old tables
> (`items_json` column, `guests` as text, ...) they will be replaced by the new ones.

Then run the schema:

```bash
mysql -u root -p foodie_db < src/main/resources/schema.sql
```

Or paste the contents of `src/main/resources/schema.sql` into your MySQL client and run it.

### 2. Configure the connection

Copy the example config file and fill in your own MySQL username:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

`application.properties` is listed in `.gitignore`, so your own password never reaches the
repository.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/foodie_db?connectionTimeZone=LOCAL&useSSL=false
spring.datasource.username=root
spring.datasource.password=your_password
```

Instead of writing the password in the file you can also pass it as an environment variable,
Spring Boot picks it up automatically:

```bash
SPRING_DATASOURCE_PASSWORD=your_password
```

### 3. Set the admin password

The admin account is created the first time the app starts. Its password is **never** written in
the code or in a properties file, it is read from the `ADMIN_PASSWORD` environment variable.

Windows (PowerShell):

```powershell
$env:ADMIN_PASSWORD = "choose-a-password"
```

macOS / Linux:

```bash
export ADMIN_PASSWORD="choose-a-password"
```

If `ADMIN_PASSWORD` is not set, the app still starts, it just does not create an admin account
(you will see a warning in the log). The admin email can be changed in
`application.properties` with `foodie.admin.email`.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

On Windows use `mvnw.cmd spring-boot:run`.

Then open:

```
http://localhost:8080/
```

`http://localhost:8080/` redirects to `food.html`. The React menu page is at
`http://localhost:8080/react/index.html` and the JSP admin page at
`http://localhost:8080/admin/orders-page?token=<your admin token>`.

### 5. Log in as admin

1. Start the app with `ADMIN_PASSWORD` set (see step 3).
2. Click **Login / Signup** in the top bar.
3. Log in with the admin email (`admin@foodies.com` unless you changed it) and the password you
   put in `ADMIN_PASSWORD`.
4. The **Admin** button now appears in the top bar and opens the dashboard.

### 6. Run the tests

The tests run on an in-memory H2 database, so **MySQL does not need to be running**:

```bash
./mvnw test
```

## API Endpoints

| Method | Endpoint                        | Who can call it | Description                                     |
| ------ | ------------------------------- | --------------- | ----------------------------------------------- |
| GET    | `/`                             | public          | Redirects to `food.html`                        |
| GET    | `/food.html`                    | public          | The shop front page                             |
| GET    | `/react/index.html`             | public          | The React menu page                             |
| GET    | `/api/menu`                     | public          | List the available menu items                   |
| POST   | `/api/auth/signup`              | public          | Create a customer account, returns a token      |
| POST   | `/api/auth/login`               | public          | Log in, returns a token                         |
| POST   | `/api/auth/logout`              | logged in       | Clears the token in the database                |
| POST   | `/api/orders`                   | logged in       | Place an order (prices come from the database)  |
| GET    | `/api/orders`                   | public          | All orders (handy for checking in the browser)  |
| GET    | `/api/orders/my`                | logged in       | Only the orders of the logged in user           |
| POST   | `/api/reservations`             | public          | Book a table (date and time are validated)      |
| GET    | `/api/admin/orders`             | admin           | All orders                                      |
| GET    | `/api/admin/reservations`       | admin           | All reservations                                |
| GET    | `/api/admin/stats`              | admin           | Number of orders and reservations               |
| PUT    | `/api/admin/orders/{id}/status` | admin           | Change the status of one order                  |
| GET    | `/admin/orders-page`            | admin           | JSP page with the latest orders (needs a token) |

Logged in and admin requests must send the token in a header:

```
Authorization: Bearer <token>
```

Only an admin can call `/api/admin/**`: no token gives **401**, a customer token gives **403**.
Validation problems give **400** with a body like `{"error": "Phone number must be exactly 10 digits"}`.

### Example: place an order

Note that there is no price or total in the body, the server reads the prices from `menu_items`.

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
        "customerName": "Sohail",
        "phone": "9876543210",
        "address": "Mumbai",
        "paymentMethod": "Cash on Delivery",
        "items": [
          { "menuItemId": 1, "quantity": 2 },
          { "menuItemId": 9, "quantity": 1 }
        ]
      }'
```

A delivery charge of 40 is added to every order.

### Example: book a table

```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
        "customerName": "Sohail",
        "phone": "9876543210",
        "reservationDate": "2026-10-20",
        "reservationTime": "19:30",
        "guests": 4
      }'
```

### Example: change an order status

```bash
curl -X PUT http://localhost:8080/api/admin/orders/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin token>" \
  -d '{ "status": "Preparing" }'
```

Allowed statuses: `Order Placed`, `Order Confirmed`, `Preparing`, `Ready`, `Out for Delivery`,
`Delivered`.


## Notes
 
- Every database table is created by `src/main/resources/schema.sql`: `users`, `menu_items`,
  `orders`, `order_items`, `reservations`.
- All SQL is in the `repository` package, the services only hold business rules.
- Passwords are stored as BCrypt hashes (only `spring-security-crypto` is used, not Spring Security).
- Tokens are random strings made with `SecureRandom` and expire after 7 days.
- The front page is served by the same app, so it calls the API with relative URLs like
  `/api/menu` and no CORS setup is needed.
- `report/AdminReportJdbc.java` has a `main` method and shows plain JDBC with `DriverManager`,
  `PreparedStatement` and try-with-resources. Run it from your IDE (or with
  `java -cp target/classes foodie_backend.foodie_backend.report.AdminReportJdbc`).

