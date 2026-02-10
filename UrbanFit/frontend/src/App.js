import Home from './pages/Home';
import Navbar from './pages/components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Product from './pages/Product';
import AdminOrders from './pages/AdminOrders';
import MyOrders from './pages/MyOrders';
import OrderDetails from './pages/OrderDetails';
import { Route, Routes, useNavigate } from "react-router-dom";
import api from './api/api';
import Cart from './pages/Cart';
import React, { useEffect, useState } from "react";
import ProductDetails from './pages/ProductDetails';
import CheckoutPage from './pages/CheckoutPage';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await api.auth.logout();
      setUser(null);
      navigate("/login");
    } catch (e) {
      alert("Logout failed");
    }
  };

  useEffect(() => {
    api.auth.me()
      .then(res => setUser(res.data))
      .catch(() => setUser(null))
      .finally(() => setAuthLoading(false));
  }, []);

  if (authLoading) {
    return <div style={{ padding: 16 }}>Loading...</div>;
  }

  return (
    <div className='routers'>
      <Navbar user={user} setUser={setUser} onLogout={handleLogout} />

        <Routes>
            <Route path="/" element={<Home user={user} />} />
            <Route path="/login" element={<Login setUser={setUser} />} />
            <Route path="/register" element={<Register />} />
            <Route path="/products" element={<Product user={user} />} />
            <Route path="/products/:id" element={<ProductDetails user={user} />} />
            <Route path="/admin/orders" element={<AdminOrders user={user} />} />
            <Route path="/orders" element={<MyOrders user={user} />} />
            <Route path="/orders/:id" element={<OrderDetails user={user} />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/checkout" element={<CheckoutPage />} />
        </Routes>
    </div>
  );
}

export default App;
