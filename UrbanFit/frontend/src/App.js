import Home from './pages/Home';
import Navbar from './pages/components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Product from './pages/Product';
import { Route, Routes } from "react-router-dom";
import api from './api/api';
import React, { useEffect, useState } from "react";
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);

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
      <Navbar user={user} setUser={setUser} />

        <Routes>
            <Route path="/" element={<Home user={user} />} />
            <Route path="/login" element={<Login setUser={setUser} />} />
            <Route path="/register" element={<Register />} />
            <Route path="/products" element={<Product user={user} />} />
        </Routes>
    </div>
  );
}

export default App;
