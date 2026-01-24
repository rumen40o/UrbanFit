import Home from './pages/Home';
import Navbar from './pages/components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import { Route, Routes } from "react-router-dom";
import React, { useEffect, useState } from "react";
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  return (
    <div className='routers'>
      <Navbar user={user} setUser={setUser} />

        <Routes>
            <Route path="/" element={<Home user={user} />} />
            <Route path="/login" element={<Login setUser={setUser} />} />
            <Route path="/register" element={<Register />} />
        </Routes>
    </div>
  );
}

export default App;
