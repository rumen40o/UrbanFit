import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./Css/Auth.css";

export default function Register() {
  const nav = useNavigate();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [phone_number, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    setErr("");
    setOk("");
    setLoading(true);

    try {
      await api.auth.register({
        username,
        email,
        password,
        phone_number,
        role: "USER",
      });

      setOk("Registered successfully. Redirecting to login...");
      setTimeout(() => nav("/login"), 600);
    } catch (e2) {
      const msg = e2?.response?.data;
      setErr(typeof msg === "string" ? msg : "Register failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth">
      <div className="auth__card">
        <h2 className="auth__title">Register</h2>

        <form className="auth__form" onSubmit={submit}>
          <label className="auth__label">
            Username
            <input
              className="auth__input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="username"
              autoComplete="username"
            />
          </label>

          <label className="auth__label">
            Email
            <input
              className="auth__input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="email"
              autoComplete="email"
            />
          </label>

          <label className="auth__label">
            Phone number
            <input
              className="auth__input"
              value={phone_number}
              onChange={(e) => setPhoneNumber(e.target.value)}
              placeholder="0888..."
              autoComplete="tel"
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
              autoComplete="new-password"
            />
          </label>

          {err && <div className="auth__error">{err}</div>}
          {ok && <div className="auth__ok">{ok}</div>}

          <button className="auth__btn auth__btn--primary" disabled={loading}>
            {loading ? "Creating..." : "Create account"}
          </button>
        </form>

        <div className="auth__footer">
          Already have an account? <Link to="/login">Login</Link>
        </div>
      </div>
    </div>
  );
}
