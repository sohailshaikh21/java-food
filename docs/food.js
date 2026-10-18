let menuItems = [];
let cart = [];
let token = localStorage.getItem("foodieToken");
let currentUser = JSON.parse(localStorage.getItem("foodieUser") || "null");
let activeCategory = "All";
let searchTerm = "";
let vegOnly = false;

const DELIVERY_CHARGE = 40;
const ORDER_STATUSES = ["Order Placed", "Order Confirmed", "Preparing", "Ready", "Out for Delivery", "Delivered"];
const TRACKING_STEPS = [
    { label: "Order Placed", note: "We have received your order." },
    { label: "Preparing", note: "Your food is being prepared." },
    { label: "Out for Delivery", note: "Your order is on the way." },
    { label: "Delivered", note: "Enjoy your meal!" }
];

function escapeHtml(value) {
    if (value === null || value === undefined) return "";
    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

const FALLBACK_MENU = [
    { id: 1, name: "Margherita Pizza", category: "Pizza", price: 199, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=700&q=80", description: "Classic tomato, mozzarella and basil pizza.", available: true },
    { id: 2, name: "Pepperoni Pizza", category: "Pizza", price: 249, foodType: "Non-Veg", imageUrl: "https://images.unsplash.com/photo-1628840042765-356cda07504e?auto=format&fit=crop&w=700&q=80", description: "Loaded with pepperoni and melted cheese.", available: true },
    { id: 3, name: "Chicken Burger", category: "Burgers", price: 149, foodType: "Non-Veg", imageUrl: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=700&q=80", description: "Juicy chicken patty with fresh vegetables.", available: true },
    { id: 4, name: "Classic Veg Burger", category: "Burgers", price: 129, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1520072959219-c595dc870360?auto=format&fit=crop&w=700&q=80", description: "Crispy veggie patty with creamy sauce.", available: true },
    { id: 5, name: "Pasta Alfredo", category: "Pasta", price: 179, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1473093295043-cdd812d0e601?auto=format&fit=crop&w=700&q=80", description: "Creamy white sauce pasta with herbs.", available: true },
    { id: 6, name: "Chicken Pasta", category: "Pasta", price: 199, foodType: "Non-Veg", imageUrl: "https://images.unsplash.com/photo-1603133872878-684f208fb84b?auto=format&fit=crop&w=700&q=80", description: "Pasta with tender chicken and rich sauce.", available: true },
    { id: 7, name: "Paneer Tikka", category: "Main Course", price: 229, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&w=700&q=80", description: "Grilled paneer with Indian spices.", available: true },
    { id: 8, name: "Chicken Biryani", category: "Main Course", price: 249, foodType: "Non-Veg", imageUrl: "https://images.unsplash.com/photo-1589302168068-964664d93dc0?auto=format&fit=crop&w=700&q=80", description: "Aromatic basmati rice with juicy chicken.", available: true },
    { id: 9, name: "French Fries", category: "Starters", price: 99, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=700&q=80", description: "Crispy golden fries with seasoning.", available: true },
    { id: 10, name: "Garlic Bread", category: "Starters", price: 89, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1619535860434-ba1d8fa12536?auto=format&fit=crop&w=700&q=80", description: "Buttery garlic bread with herbs.", available: true },
    { id: 11, name: "Chocolate Lava Cake", category: "Desserts", price: 129, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=700&q=80", description: "Warm chocolate cake with molten center.", available: true },
    { id: 12, name: "Cheesecake", category: "Desserts", price: 149, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1565958011703-44f9829ba187?auto=format&fit=crop&w=700&q=80", description: "Creamy cheesecake with fresh topping.", available: true },
    { id: 13, name: "Cold Coffee", category: "Beverages", price: 99, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?auto=format&fit=crop&w=700&q=80", description: "Chilled creamy coffee with ice.", available: true },
    { id: 14, name: "Fresh Lime Soda", category: "Beverages", price: 79, foodType: "Veg", imageUrl: "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=700&q=80", description: "Refreshing lime drink served chilled.", available: true }
];

const API_BASE_URL = (function() {
    if (window.FOODIE_API_URL) return window.FOODIE_API_URL.replace(/\/$/, "");
    const custom = localStorage.getItem("foodieApiUrl");
    if (custom) return custom.replace(/\/$/, "");
    if (window.location.hostname.endsWith("github.io")) {
        return "https://sohail-foodie-api.loca.lt";
    }
    return "";
})();

function getApiUrl(url) {
    if (url.startsWith("http://") || url.startsWith("https://")) return url;
    return API_BASE_URL ? `${API_BASE_URL}${url}` : url;
}

function getDemoOrders() {
    try {
        return JSON.parse(localStorage.getItem("foodieDemoOrders") || "[]");
    } catch {
        return [];
    }
}

function saveDemoOrder(order) {
    const orders = getDemoOrders();
    orders.unshift(order);
    localStorage.setItem("foodieDemoOrders", JSON.stringify(orders));
}

function updateDemoOrderStatus(id, status) {
    const orders = getDemoOrders();
    const item = orders.find(o => String(o.id) === String(id));
    if (item) {
        item.status = status;
        localStorage.setItem("foodieDemoOrders", JSON.stringify(orders));
    }
}

function getDemoReservations() {
    try {
        return JSON.parse(localStorage.getItem("foodieDemoReservations") || "[]");
    } catch {
        return [];
    }
}

function saveDemoReservation(reservation) {
    const list = getDemoReservations();
    list.unshift(reservation);
    localStorage.setItem("foodieDemoReservations", JSON.stringify(list));
}

function checkBackendStatus() {
    const banner = document.getElementById("backendBanner");
    if (!banner) return;
    banner.style.display = "none";
}

function openBackendConfig() {
    const modal = document.getElementById("backendModal");
    if (modal) {
        const input = document.getElementById("backendUrlInput");
        if (input) input.value = localStorage.getItem("foodieApiUrl") || API_BASE_URL || "";
        modal.classList.add("active");
    }
}

function saveBackendUrl() {
    const input = document.getElementById("backendUrlInput");
    const url = input ? input.value.trim().replace(/\/$/, "") : "";
    if (url) {
        localStorage.setItem("foodieApiUrl", url);
        showToast("Backend URL saved! Connecting...");
    } else {
        localStorage.removeItem("foodieApiUrl");
        showToast("Reset to Default Live URL.");
    }
    closeModal("backendModal");
    setTimeout(() => window.location.reload(), 1000);
}

function resetBackendUrl() {
    localStorage.removeItem("foodieApiUrl");
    showToast("Reset to Default Live URL.");
    closeModal("backendModal");
    setTimeout(() => window.location.reload(), 1000);
}

async function apiFetch(url, options = {}) {
    const headers = { 
        "Content-Type": "application/json",
        "bypass-tunnel-reminder": "true"
    };
    if (token) headers["Authorization"] = "Bearer " + token;

    const fullUrl = getApiUrl(url);
    const response = await fetch(fullUrl, { ...options, headers });
    const data = await response.json().catch(() => ({}));

    if (response.status === 401) {
        clearSession();
        openAuth();
        throw new Error(data.error || "Please log in again.");
    }
    if (!response.ok) {
        throw new Error(data.error || "Something went wrong.");
    }
    return data;
}

async function loadMenu() {
    try {
        menuItems = await apiFetch("/api/menu");
    } catch (error) {
        console.warn("Backend API not reachable, loaded default menu for preview:", error);
        menuItems = FALLBACK_MENU;
    }
    renderMenu();
}

function renderMenu() {
    const grid = document.getElementById("menuGrid");
    const list = menuItems.filter(item => {
        const matchesCategory = activeCategory === "All" || item.category === activeCategory;
        const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesType = !vegOnly || item.foodType === "Veg";
        return matchesCategory && matchesSearch && matchesType;
    });

    if (list.length === 0) {
        grid.innerHTML = `<div class="empty">No dishes found. Try another search.</div>`;
        return;
    }

    grid.innerHTML = list.map(item => `
        <div class="food-card">
            <div class="food-image">
                <img src="${escapeHtml(item.imageUrl)}" alt="${escapeHtml(item.name)}">
                <span class="food-badge ${item.foodType === "Veg" ? "veg" : "nonveg"}">● ${escapeHtml(item.foodType)}</span>
            </div>
            <div class="food-content">
                <h3>${escapeHtml(item.name)}</h3>
                <p class="food-description">${escapeHtml(item.description)}</p>
                <div class="food-bottom">
                    <span class="price">₹${item.price}</span>
                    <button class="add-btn" onclick="addToCart(${item.id})">Add</button>
                </div>
            </div>
        </div>
    `).join("");
}

function filterCategory(category, button) {
    activeCategory = category;
    document.querySelectorAll(".category-btn").forEach(btn => btn.classList.remove("active"));
    button.classList.add("active");
    renderMenu();
}

function handleSearch(value) {
    searchTerm = value.trim();
    renderMenu();
}

function toggleVegOnly(checked) {
    vegOnly = checked;
    renderMenu();
}

function addToCart(id) {
    const item = menuItems.find(dish => dish.id === id);
    if (!item) return;

    const existing = cart.find(dish => dish.id === id);
    if (existing) {
        existing.quantity += 1;
    } else {
        cart.push({ ...item, quantity: 1 });
    }
    updateCartCount();
    showToast(item.name + " added to cart!");
}

function updateCartCount() {
    const total = cart.reduce((sum, item) => sum + item.quantity, 0);
    document.getElementById("cartCount").textContent = total;
}

function openCart() {
    renderCart();
    document.getElementById("cartModal").classList.add("active");
}

function renderCart() {
    const itemsContainer = document.getElementById("cartItems");
    const subtotalEl = document.getElementById("cartSubtotal");
    const totalEl = document.getElementById("cartTotal");

    if (cart.length === 0) {
        itemsContainer.innerHTML = `<div class="cart-empty">Your cart is empty. Add delicious food from the menu!</div>`;
        subtotalEl.textContent = "₹0";
        totalEl.textContent = "₹0";
        return;
    }

    const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const total = subtotal + DELIVERY_CHARGE;

    itemsContainer.innerHTML = cart.map(item => `
        <div class="cart-item">
            <div class="cart-item-info">
                <strong>${escapeHtml(item.name)}</strong>
                <span class="cart-item-price">₹${item.price} × ${item.quantity} = ₹${item.price * item.quantity}</span>
            </div>
            <div class="cart-item-actions">
                <button onclick="changeQuantity(${item.id}, -1)">-</button>
                <span>${item.quantity}</span>
                <button onclick="changeQuantity(${item.id}, 1)">+</button>
                <button class="remove-btn" onclick="removeFromCart(${item.id})">✕</button>
            </div>
        </div>
    `).join("");

    subtotalEl.textContent = "₹" + subtotal;
    totalEl.textContent = "₹" + total;
}

function changeQuantity(id, delta) {
    const item = cart.find(dish => dish.id === id);
    if (!item) return;

    item.quantity += delta;
    if (item.quantity <= 0) {
        cart = cart.filter(dish => dish.id !== id);
    }
    updateCartCount();
    renderCart();
}

function removeFromCart(id) {
    cart = cart.filter(dish => dish.id !== id);
    updateCartCount();
    renderCart();
}

function openCheckout() {
    if (cart.length === 0) {
        showToast("Add some food to cart first!");
        return;
    }
    closeModal("cartModal");

    const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const total = subtotal + DELIVERY_CHARGE;

    const totalEl = document.getElementById("checkoutTotal");
    if (totalEl) totalEl.textContent = "₹" + total;

    const amountEl = document.getElementById("checkoutAmount");
    if (amountEl) amountEl.innerHTML = `Total Payable: <strong style="color:#ff4757">₹${total}</strong>`;

    if (currentUser) {
        const nameInput = document.getElementById("checkoutName");
        if (nameInput) nameInput.value = currentUser.name || "";
    }

    const modal = document.getElementById("checkoutModal");
    if (modal) {
        modal.classList.add("active");
    }
}

function checkRequired(inputId, fieldName) {
    const value = document.getElementById(inputId).value.trim();
    const ok = value.length > 0;
    showFieldError(inputId, ok ? "" : fieldName + " is required.");
    return ok;
}

function showFieldError(inputId, message) {
    const errorEl = document.getElementById(inputId + "Error");
    if (errorEl) errorEl.textContent = message;
}

function checkPhone(inputId) {
    const value = document.getElementById(inputId).value.trim();
    const ok = /^\d{10}$/.test(value);
    showFieldError(inputId, ok ? "" : "Enter a valid 10 digit phone number.");
    return ok;
}

async function handleCheckout(event) {
    event.preventDefault();

    let valid = true;
    if (!checkRequired("checkoutName", "Name")) valid = false;
    if (!checkPhone("checkoutPhone")) valid = false;
    if (!checkRequired("checkoutAddress", "Address")) valid = false;
    if (!checkRequired("paymentMethod", "Payment method")) valid = false;
    if (!valid) return;

    const body = {
        customerName: document.getElementById("checkoutName").value.trim(),
        phone: document.getElementById("checkoutPhone").value.trim(),
        address: document.getElementById("checkoutAddress").value.trim(),
        paymentMethod: document.getElementById("paymentMethod").value,
        items: cart.map(item => ({ menuItemId: item.id, quantity: item.quantity }))
    };

    const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const orderItemsSnapshot = cart.map(item => ({
        itemName: item.name,
        quantity: item.quantity,
        unitPrice: item.price
    }));

    try {
        const order = await apiFetch("/api/orders", { method: "POST", body: JSON.stringify(body) });
        showToast("Order placed successfully! 🎉 (Order #" + order.id + ")");
        cart = [];
        updateCartCount();
        document.getElementById("checkoutForm").reset();
        closeModal("checkoutModal");
        renderTracking(order);
    } catch (error) {
        // Fallback demo order for GitHub Pages preview
        const demoOrder = {
            id: Math.floor(1000 + Math.random() * 9000),
            customerName: body.customerName,
            phone: body.phone,
            address: body.address,
            paymentMethod: body.paymentMethod,
            deliveryCharge: DELIVERY_CHARGE,
            totalAmount: subtotal + DELIVERY_CHARGE,
            status: "Order Placed",
            orderDate: new Date().toISOString(),
            items: orderItemsSnapshot
        };
        saveDemoOrder(demoOrder);
        cart = [];
        updateCartCount();
        document.getElementById("checkoutForm").reset();
        closeModal("checkoutModal");
        showToast("Order #" + demoOrder.id + " placed! (Demo Mode: MySQL connection needed for database storage)");
        renderTracking(demoOrder);
    }
}

async function handleReservation(event) {
    event.preventDefault();

    let valid = true;
    if (!checkRequired("reserveDate", "Date")) valid = false;
    if (!checkRequired("reserveTime", "Time")) valid = false;
    if (!checkRequired("guestCount", "Number of guests")) valid = false;
    if (!checkRequired("reserveName", "Name")) valid = false;
    if (!checkPhone("reservePhone")) valid = false;
    if (!valid) return;

    const body = {
        customerName: document.getElementById("reserveName").value.trim(),
        phone: document.getElementById("reservePhone").value.trim(),
        reservationDate: document.getElementById("reserveDate").value,
        reservationTime: document.getElementById("reserveTime").value,
        guests: Number(document.getElementById("guestCount").value)
    };

    try {
        await apiFetch("/api/reservations", { method: "POST", body: JSON.stringify(body) });
        showToast("Table reserved successfully! 🍽️");
        document.getElementById("reservationForm").reset();
    } catch (error) {
        saveDemoReservation({
            id: Math.floor(100 + Math.random() * 900),
            ...body
        });
        showToast("Table reserved for " + escapeHtml(body.customerName) + "! (Demo Mode: MySQL needed for database storage)");
        document.getElementById("reservationForm").reset();
    }
}

function openAuth() {
    document.getElementById("authModal").classList.add("active");
}

function showAuthTab(tab, button) {
    document.querySelectorAll(".auth-tab").forEach(btn => btn.classList.remove("active"));
    button.classList.add("active");
    document.getElementById("loginForm").style.display = tab === "login" ? "block" : "none";
    document.getElementById("signupForm").style.display = tab === "signup" ? "block" : "none";
}

function saveSession(data) {
    token = data.token;
    currentUser = data.user;
    localStorage.setItem("foodieToken", token);
    localStorage.setItem("foodieUser", JSON.stringify(currentUser));
    updateAuthUI();
}

function clearSession() {
    token = null;
    currentUser = null;
    localStorage.removeItem("foodieToken");
    localStorage.removeItem("foodieUser");
    updateAuthUI();
}

function updateAuthUI() {
    const greeting = document.getElementById("userGreeting");
    const authBtn = document.getElementById("authNavBtn");
    const logoutBtn = document.getElementById("logoutNavBtn");
    const adminBtn = document.getElementById("adminNavBtn");

    if (currentUser) {
        greeting.textContent = "Hi, " + currentUser.name;
        authBtn.style.display = "none";
        logoutBtn.style.display = "inline-block";
        adminBtn.style.display = currentUser.role === "ADMIN" ? "inline-block" : "none";
    } else {
        greeting.textContent = "";
        authBtn.style.display = "inline-block";
        logoutBtn.style.display = "none";
        adminBtn.style.display = "none";
    }
}

async function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById("loginEmail").value.trim();
    const password = document.getElementById("loginPassword").value;

    try {
        const data = await apiFetch("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({ email, password })
        });
        saveSession(data);
        document.getElementById("loginForm").reset();
        closeModal("authModal");
        showToast("Welcome back, " + data.user.name + "!");
        if (data.user.role === "ADMIN") openAdminDashboard();
    } catch (error) {
        // Fallback demo login for GitHub Pages
        const isAdmin = email.toLowerCase().includes("admin");
        const namePart = email.split("@")[0] || "User";
        const capitalized = namePart.charAt(0).toUpperCase() + namePart.slice(1);
        const demoUser = {
            id: Math.floor(100 + Math.random() * 900),
            name: isAdmin ? "Restaurant Admin" : capitalized,
            email: email,
            role: isAdmin ? "ADMIN" : "CUSTOMER"
        };
        const demoData = {
            token: "demo-jwt-" + Date.now(),
            user: demoUser
        };
        saveSession(demoData);
        document.getElementById("loginForm").reset();
        closeModal("authModal");
        showToast("Logged in as " + demoUser.name + " (" + (isAdmin ? "Admin Mode" : "Demo Mode") + ") 🎉");
        if (demoUser.role === "ADMIN") openAdminDashboard();
    }
}

async function handleSignup(event) {
    event.preventDefault();
    const name = document.getElementById("signupName").value.trim();
    const email = document.getElementById("signupEmail").value.trim();
    const password = document.getElementById("signupPassword").value;

    try {
        const data = await apiFetch("/api/auth/signup", {
            method: "POST",
            body: JSON.stringify({ name, email, password })
        });
        saveSession(data);
        document.getElementById("signupForm").reset();
        closeModal("authModal");
        showToast("Welcome, " + data.user.name + "!");
    } catch (error) {
        const demoUser = {
            id: Math.floor(100 + Math.random() * 900),
            name: name || "Customer",
            email: email,
            role: "CUSTOMER"
        };
        const demoData = {
            token: "demo-jwt-" + Date.now(),
            user: demoUser
        };
        saveSession(demoData);
        document.getElementById("signupForm").reset();
        closeModal("authModal");
        showToast("Welcome, " + demoUser.name + "! (Demo Mode)");
    }
}

async function logout() {
    try {
        await apiFetch("/api/auth/logout", { method: "POST" });
    } catch (error) {
    }
    clearSession();
    closeModal("adminModal");
    closeModal("authModal");
    showToast("Logged out.");
}

async function openAdminDashboard() {
    if (!currentUser || currentUser.role !== "ADMIN") {
        showToast("Admin access only.");
        return;
    }
    document.getElementById("adminModal").classList.add("active");
    await Promise.all([loadAdminOrders(), loadAdminReservations(), loadAdminStats()]);
}

async function loadAdminStats() {
    try {
        const stats = await apiFetch("/api/admin/stats");
        document.getElementById("adminOrders").textContent = stats.orders;
        document.getElementById("adminReservations").textContent = stats.reservations;
    } catch (error) {
        const orders = getDemoOrders();
        const reservations = getDemoReservations();
        document.getElementById("adminOrders").textContent = orders.length;
        document.getElementById("adminReservations").textContent = reservations.length;
    }
}

async function loadAdminOrders() {
    const table = document.getElementById("adminOrderList");
    table.innerHTML = `<tr><td colspan="3" style="text-align:center">Loading...</td></tr>`;
    try {
        const orders = await apiFetch("/api/admin/orders");
        renderAdminOrdersTable(orders);
    } catch (error) {
        const demoOrders = getDemoOrders();
        renderAdminOrdersTable(demoOrders);
    }
}

function renderAdminOrdersTable(orders) {
    const table = document.getElementById("adminOrderList");
    if (!orders || orders.length === 0) {
        table.innerHTML = `<tr><td colspan="3" style="text-align:center">No orders yet</td></tr>`;
        return;
    }
    table.innerHTML = orders.map(order => `
        <tr>
            <td>${escapeHtml(order.customerName)}</td>
            <td>₹${order.totalAmount}</td>
            <td>
                <select class="status-select" onchange="changeOrderStatus(${order.id}, this.value)">
                    ${statusOptions(order.status)}
                </select>
            </td>
        </tr>`).join("");
}

function statusOptions(current) {
    return ORDER_STATUSES.map(status =>
        `<option value="${status}" ${status === current ? "selected" : ""}>${status}</option>`).join("");
}

async function changeOrderStatus(id, status) {
    try {
        await apiFetch("/api/admin/orders/" + id + "/status", {
            method: "PUT",
            body: JSON.stringify({ status })
        });
        showToast("Order #" + id + " is now " + status);
    } catch (error) {
        updateDemoOrderStatus(id, status);
        showToast("Order #" + id + " status: " + status + " (Demo Mode)");
    }
}

async function loadAdminReservations() {
    const table = document.getElementById("adminReservationList");
    table.innerHTML = `<tr><td colspan="5" style="text-align:center">Loading...</td></tr>`;
    try {
        const reservations = await apiFetch("/api/admin/reservations");
        renderAdminReservationsTable(reservations);
    } catch (error) {
        const demoReservations = getDemoReservations();
        renderAdminReservationsTable(demoReservations);
    }
}

function renderAdminReservationsTable(reservations) {
    const table = document.getElementById("adminReservationList");
    if (!reservations || reservations.length === 0) {
        table.innerHTML = `<tr><td colspan="5" style="text-align:center">No reservations yet</td></tr>`;
        return;
    }
    table.innerHTML = reservations.map(r => `
        <tr>
            <td>${escapeHtml(r.customerName)}</td>
            <td>${escapeHtml(r.phone)}</td>
            <td>${escapeHtml(r.reservationDate)}</td>
            <td>${escapeHtml(r.reservationTime)}</td>
            <td>${r.guests}</td>
        </tr>`).join("");
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove("active");
}

function showToast(message) {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => {
        toast.classList.remove("show");
    }, 3500);
}

function formatOrderDate(isoString) {
    if (!isoString) return "";
    const date = new Date(isoString);
    if (isNaN(date.getTime())) return isoString;
    return date.toLocaleDateString("en-IN", {
        day: "numeric",
        month: "short",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function orderItemsText(order) {
    if (!order.items || order.items.length === 0) return "No item details";
    return order.items.map(item => `${item.itemName} × ${item.quantity}`).join(", ");
}

function orderCard(order) {
    return `
        <div class="order-card">
            <div class="order-card-header">
                <strong>Order #${order.id}</strong>
                <span class="order-status">${escapeHtml(order.status)}</span>
            </div>
            <p>${escapeHtml(formatOrderDate(order.orderDate))} · ₹${order.totalAmount}</p>
            <p>${escapeHtml(orderItemsText(order))}</p>
            <div class="order-card-footer">
                <span class="order-status">${escapeHtml(order.paymentMethod)}</span>
                <button class="track-button" onclick="trackOrder(${order.id})">Track Order</button>
            </div>
        </div>`;
}

async function showOrders() {
    if (!token) {
        openAuth();
        return;
    }
    const container = document.getElementById("ordersContent");
    container.innerHTML = `<div class="orders-empty">Loading orders...</div>`;
    document.getElementById("ordersModal").classList.add("active");

    try {
        const orders = await apiFetch("/api/orders/my");
        if (orders.length === 0) {
            container.innerHTML = `<div class="orders-empty">You have not placed any order yet.</div>`;
            return;
        }
        container.innerHTML = `<div class="orders-list">` + orders.map(orderCard).join("") + `</div>`;
    } catch (error) {
        const demoOrders = getDemoOrders();
        if (demoOrders.length === 0) {
            container.innerHTML = `<div class="orders-empty">No orders placed yet in this session. Add items to cart and checkout!</div>`;
            return;
        }
        container.innerHTML = `<div class="orders-list">` + demoOrders.map(orderCard).join("") + `</div>`;
    }
}

async function trackLatestOrder() {
    if (!token) {
        openAuth();
        return;
    }
    try {
        const orders = await apiFetch("/api/orders/my");
        if (orders && orders.length > 0) {
            renderTracking(orders[0]);
            return;
        }
    } catch (error) {
    }
    const demoOrders = getDemoOrders();
    if (demoOrders.length > 0) {
        renderTracking(demoOrders[0]);
    } else {
        showToast("No orders placed yet to track!");
    }
}

async function trackOrder(id) {
    try {
        const orders = await apiFetch("/api/orders/my");
        const order = orders.find(item => String(item.id) === String(id));
        if (order) {
            renderTracking(order);
            return;
        }
    } catch (error) {
    }
    const demoOrders = getDemoOrders();
    const order = demoOrders.find(item => String(item.id) === String(id));
    if (order) {
        renderTracking(order);
    } else {
        showToast("Order not found!");
    }
}

function statusStepIndex(status) {
    const index = ORDER_STATUSES.indexOf(status);
    if (index <= 1) return 0;
    if (index === 2) return 1;
    if (index <= 4) return 2;
    return 3;
}

function renderTracking(order) {
    closeModal("ordersModal");

    const currentStep = statusStepIndex(order.status);
    const items = order.items || [];

    document.getElementById("trackingTitle").textContent = "Order #" + order.id;
    document.getElementById("trackingContent").innerHTML = `
        <div class="tracking-summary">
            <span>Order ID</span><strong>#${order.id}</strong>
            <span>Placed On</span><strong>${escapeHtml(formatOrderDate(order.orderDate))}</strong>
            <span>Payment</span><strong>${escapeHtml(order.paymentMethod)}</strong>
            <span>Total</span><strong>₹${order.totalAmount}</strong>
            <span>Status</span><strong>${escapeHtml(order.status)}</strong>
        </div>
        <ul class="tracking-timeline">
            ${TRACKING_STEPS.map((step, index) => {
                const state = index < currentStep ? "done" : (index === currentStep ? "current" : "");
                return `<li class="tracking-step ${state}">
                            <div class="tracking-dot">${index < currentStep ? "✓" : index + 1}</div>
                            <div><strong>${step.label}</strong><p>${step.note}</p></div>
                        </li>`;
            }).join("")}
        </ul>
        <div class="tracking-items">
            ${items.length === 0
                ? `<div class="tracking-item"><span>No items</span><span>₹0</span></div>`
                : items.map(item => `<div class="tracking-item">
                        <span>${escapeHtml(item.itemName)} × ${item.quantity}</span>
                        <span>₹${item.unitPrice * item.quantity}</span>
                    </div>`).join("")}
        </div>`;

    document.getElementById("trackingModal").classList.add("active");
}

document.getElementById("checkoutForm").addEventListener("submit", handleCheckout);
document.getElementById("reservationForm").addEventListener("submit", handleReservation);
document.getElementById("loginForm").addEventListener("submit", handleLogin);
document.getElementById("signupForm").addEventListener("submit", handleSignup);

checkBackendStatus();
loadMenu();
updateCartCount();
updateAuthUI();
