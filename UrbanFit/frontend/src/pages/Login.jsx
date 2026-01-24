import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./Css/Auth.css";

export default function Login({ setUser }) {
  const navigate = useNavigate();
  const location = useLocation();

  const [identifier, setIdentifier] = useState(""); // username OR email
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      // 🔑 ВАЖНО: backend иска identifier, не email
      await api.auth.login({
        identifier,
        password,
      });

      // дърпаме логнатия потребител
      const me = await api.auth.me();
      setUser(me.data);

      // redirect
      const redirectTo = location.state?.from || "/";
      navigate(redirectTo, { replace: true });
    } catch (err) {
      const msg = err?.response?.data;
      setError(typeof msg === "string" ? msg : "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth">
      <div className="auth__card">
        <h2 className="auth__title">Login</h2>

        <form className="auth__form" onSubmit={handleSubmit}>
          <label className="auth__label">
            Username or Email
            <input
              className="auth__input"
              value={identifier}
              onChange={(e) => setIdentifier(e.target.value)}
              placeholder="username or email"
              autoComplete="username"
              required
            />
          </label>

          <label className="auth__label">
            Password
            <input
              className="auth__input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="password"
              autoComplete="current-password"
              required
            />
          </label>

          {error && <div className="auth__error">{error}</div>}

          <button
            type="submit"
            className="auth__btn auth__btn--primary"
            disabled={loading}
          >
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <div className="auth__footer">
          No account? <Link to="/register">Register</Link>
        </div>
      </div>
    </div>
  );
}
