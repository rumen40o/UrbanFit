import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import api from "../../api/api.js";
import "./css/Navbar.css";

export default function Navbar({ user, setUser }) {
  const nav = useNavigate();
  const location = useLocation();

  const [q, setQ] = useState("");
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    setQ(params.get("q") || "");
  }, [location.search]);

  const onSearchSubmit = (e) => {
    e.preventDefault();
    const query = q.trim();
    nav(query ? `/products?q=${encodeURIComponent(query)}` : "/products");
  };

  const logout = async () => {
    await api.auth.logout().catch(() => {});
    setUser(null);
    setMenuOpen(false);
    nav("/");
  };

  return (
    <header className="navbar">
      <Link to="/" className="navbar__logo">UrbanFit</Link>

      <form className="navbar__search" onSubmit={onSearchSubmit}>
        <input
          className="navbar__search-input"
          value={q}
          onChange={(e) => setQ(e.target.value)}
          placeholder="Search products..."
        />
        <button className="navbar__search-btn">Search</button>
      </form>

      {!user ? (
        <div className="navbar__auth">
          <Link to="/login"><button className="btn">Login</button></Link>
          <Link to="/register"><button className="btn btn--primary">Register</button></Link>
        </div>
      ) : (
        <div className="navbar__user">
          <button
            className="navbar__user-btn"
            onClick={() => setMenuOpen(v => !v)}
          >
            👤
          </button>

          {menuOpen && (
            <div className="navbar__dropdown">
              <div className="navbar__dropdown-user">
                <strong>{user.username}</strong>
                <span>{user.role}</span>
              </div>

              <button onClick={() => nav("/products")}>Products</button>

              {user.role === "ADMIN" && (
                <button onClick={() => nav("/admin/orders")}>Admin Orders</button>
              )}

              <button className="danger" onClick={logout}>Logout</button>
            </div>
          )}
        </div>
      )}
    </header>
  );
}
