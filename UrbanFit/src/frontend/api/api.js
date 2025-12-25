import axios from "axios";

const http = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // важно за jwt_token cookie
});

export const api = {
  // ---------- AUTH ----------
  auth: {
    register: (data) => http.post("/auth/register", data),
    login: (data) => http.post("/auth/login", data),
    logout: () => http.post("/auth/logout"),
    me: () => http.get("/auth/logged/user"),
  },

  // ---------- CATEGORIES ----------
  categories: {
    list: () => http.get("/categories"),
    create: (data) => http.post("/categories/add", data), // ADMIN
    update: (id, data) => http.put(`/categories/${id}`, data), // ADMIN (ако така ти е endpoint-а)
    remove: (id) => http.delete(`/categories/${id}`), // ADMIN
  },

  // ---------- PRODUCTS ----------
  products: {
    list: (params) => http.get("/products", { params }),
    get: (id) => http.get(`/products/${id}`),
    create: (data) => http.post("/products/add", data), // ADMIN
    update: (id, data) => http.put(`/products/${id}`, data), // ADMIN
    remove: (id) => http.delete(`/products/${id}`), // ADMIN
  },

  // ---------- CART ----------
  cart: {
    get: () => http.get("/cart"),
    add: (data) => http.post("/cart/add", data),
    updateQty: (productId, data) => http.patch(`/cart/item/${productId}`, data),
    remove: (productId) => http.delete(`/cart/item/${productId}`),
    clear: () => http.delete("/cart/clear"),
  },

  // ---------- ORDERS ----------
  orders: {
    checkout: (data) => http.post("/orders/checkout", data),
    my: () => http.get("/orders/my"),
    get: (id) => http.get(`/orders/${id}`),
  },

  // ---------- ADMIN ORDERS ----------
  adminOrders: {
    list: (params) => http.get("/admin/orders", { params }),
    get: (id) => http.get(`/admin/orders/${id}`),
    updateStatus: (id, data) => http.patch(`/admin/orders/${id}/status`, data),
  },

  // ---------- REVIEWS ----------
  reviews: {
    byProduct: (productId) => http.get(`/reviews/product/${productId}`),
    createOrUpdate: (data) => http.post("/reviews", data),
    deleteMine: (productId) => http.delete(`/reviews/product/${productId}`),
  },
};

export default api;