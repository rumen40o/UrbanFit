import { useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../../api/api";
import "./css/Navbar.css";

export default function Navbar({ user, onLogout }) {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");

  // cart dropdown state
  const [openCart, setOpenCart] = useState(false);
  const [cart, setCart] = useState(null);
  const [loadingCart, setLoadingCart] = useState(false);
  const [busyId, setBusyId] = useState(null); // variantId
  const cartRef = useRef(null);
  const [openUser, setOpenUser] = useState(false);
  const userRef = useRef(null);

  const getInitials = (name) => {
    if (!name) return "?";
    const parts = String(name).split(/\s+/).filter(Boolean);
    const a = parts[0] ? parts[0].charAt(0).toUpperCase() : "";
    const b = parts[1] ? parts[1].charAt(0).toUpperCase() : "";
    return `${a}${b}` || String(name).charAt(0).toUpperCase();
  };

  const items = cart?.items || [];
  const total = Number(cart?.total || 0);

  const loadCart = async () => {
    if (!user) return; // cart е само за логнати (твоят backend е isAuthenticated)
    setLoadingCart(true);
    try {
      const res = await api.cart.get();
      setCart(res.data);
    } catch (e) {
      setCart({ items: [], total: 0 });
    } finally {
      setLoadingCart(false);
    }
  };

  // close on outside click
  useEffect(() => {
    const onDown = (e) => {
      if (!cartRef.current) return;
      if (!cartRef.current.contains(e.target)) setOpenCart(false);
    };
    document.addEventListener("mousedown", onDown);
    return () => document.removeEventListener("mousedown", onDown);
  }, []);

  // close user dropdown on outside click
  useEffect(() => {
    const onDownUser = (e) => {
      if (!userRef.current) return;
      if (!userRef.current.contains(e.target)) setOpenUser(false);
    };
    document.addEventListener("mousedown", onDownUser);
    return () => document.removeEventListener("mousedown", onDownUser);
  }, []);

  // when open -> load cart
  useEffect(() => {
    if (openCart) loadCart();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [openCart]);

  const inc = async (variantId, currentQty) => {
    setBusyId(variantId);
    try {
      await api.cart.updateQty(variantId, { qty: currentQty + 1 });
      await loadCart();
    } catch (e) {
      console.error("Failed to increase qty", e);
      const msg = e?.response?.data || e?.message || "Failed";
      alert(typeof msg === "string" ? msg : JSON.stringify(msg));
    } finally {
      setBusyId(null);
    }
  };

  const dec = async (variantId, currentQty) => {
    if (currentQty <= 1) return;
    setBusyId(variantId);
    try {
      await api.cart.updateQty(variantId, { qty: currentQty - 1 });
      await loadCart();
    } catch (e) {
      console.error("Failed to decrease qty", e);
      const msg = e?.response?.data || e?.message || "Failed";
      alert(typeof msg === "string" ? msg : JSON.stringify(msg));
    } finally {
      setBusyId(null);
    }
  };

  const remove = async (variantId) => {
    setBusyId(variantId);
    try {
      await api.cart.remove(variantId);
      await loadCart();
    } catch (e) {
      console.error("Failed to remove item", e);
      const msg = e?.response?.data || e?.message || "Failed";
      alert(typeof msg === "string" ? msg : JSON.stringify(msg));
    } finally {
      setBusyId(null);
    }
  };

  const goCart = () => {
    setOpenCart(false);
    navigate("/cart");
  };

  const goCheckout = () => {
    setOpenCart(false);
    navigate("/checkout");
  };

  return (
    <div className="nav">
      <div className="nav__inner">
        <Link to="/" className="nav__brand">UrbanFit</Link>

        <div className="nav__links">
          <Link to="/products" className="nav__link">Products</Link>
        </div>

        <form
          className="nav__search"
          onSubmit={(e) => {
            e.preventDefault();
            const q = (search || "").trim();
            navigate(q ? `/products?q=${encodeURIComponent(q)}` : "/products");
            setSearch("");
          }}
        >
          <input
            className="nav__searchInput"
            type="search"
            placeholder="Search products..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <button type="submit" className="nav__searchBtn">🔍</button>
        </form>

        <div className="nav__right">
          {/* CART ICON */}
          <div className="navCart" ref={cartRef}>
            <button
              className="navCart__btn"
              onClick={() => {
                if (!user) return navigate("/login");
                setOpenCart((v) => !v);
              }}
              title="Cart"
            >
              🛒
              {user && items.length > 0 && <span className="navCart__badge">{items.length}</span>}
            </button>

            {openCart && (
              <div className="navCart__dropdown">
                <div className="navCart__head">
                  <div className="navCart__title">Cart</div>
                  {loadingCart && <div className="navCart__muted">Loading...</div>}
                </div>

                {!loadingCart && items.length === 0 && (
                  <div className="navCart__empty">Cart is empty.</div>
                )}

                {!loadingCart && items.length > 0 && (
                  <>
                    <div className="navCart__list">
                      {items.map((it) => (
                        <div key={it.variantId} className="navCartItem">
                          <div className="navCartItem__main">
                            <div className="navCartItem__name">
                              {it.productName} <span className="navCartItem__size">({it.size})</span>
                            </div>
                            <div className="navCartItem__price">
                              {Number(it.unitPrice).toFixed(2)} € / pcs
                            </div>
                          </div>

                          <div className="navCartItem__qty">
                            <button
                              onClick={() => dec(it.variantId, it.qty)}
                              disabled={busyId === it.variantId || it.qty <= 1}
                            >
                              -
                            </button>
                            <span>{it.qty}</span>
                            <button
                              onClick={() => inc(it.variantId, it.qty)}
                              disabled={busyId === it.variantId}
                            >
                              +
                            </button>
                          </div>

                          <div className="navCartItem__total">
                            {Number(it.lineTotal).toFixed(2)} €
                          </div>

                          <button
                            className="navCartItem__remove"
                            onClick={() => remove(it.variantId)}
                            disabled={busyId === it.variantId}
                          >
                            ✕
                          </button>
                        </div>
                      ))}
                    </div>

                    <div className="navCart__footer">
                      <div className="navCart__sum">
                        <span>Total</span>
                        <b>{total.toFixed(2)} €</b>
                      </div>

                      <div className="navCart__actions">
                        <button className="btn" onClick={goCart}>View cart</button>
                        <button className="btn btn--primary" onClick={goCheckout}>Checkout</button>
                      </div>
                    </div>
                  </>
                )}
              </div>
            )}
          </div>

          {/* USER AREA */}
          {user ? (
            <div className="nav__user" ref={userRef}>
              <button
                className="nav__avatarBtn"
                title={user.username || user.email}
                onClick={() => setOpenUser((v) => !v)}
              >
                {getInitials(user.username || user.email)}
              </button>

              {openUser && (
                <div className="nav__userDropdown">
                  <div className="nav__userInfo">
                    <div className="nav__avatarSmall">{getInitials(user.username || user.email)}</div>
                    <div>
                      <div className="nav__username">{user.username || user.email}</div>
                      {user.email && <div className="nav__email">{user.email}</div>}
                    </div>
                  </div>
                  <div style={{ paddingTop: 8 }}>
                    <button
                      className="btn btn--primary"
                      onClick={() => { setOpenUser(false); onLogout(); }}
                    >
                      Logout
                    </button>
                  </div>
                </div>
              )}
            </div>
          ) : (
            <div className="nav__auth">
              <Link className="btn" to="/login">Login</Link>
              <Link className="btn btn--primary" to="/register">Register</Link>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
