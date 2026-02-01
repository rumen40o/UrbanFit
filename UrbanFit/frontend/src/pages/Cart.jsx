import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./Css/Cart.css";

export default function Cart() {
  const navigate = useNavigate();

  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  const load = () => {
    setLoading(true);
    setErr("");
    api.cart
      .get()
      .then((res) => setCart(res.data))
      .catch((e) => {
        const msg = e?.response?.data;
        setErr(typeof msg === "string" ? msg : "Failed to load cart");
        setCart(null);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const items = cart?.items || [];

  const total = useMemo(() => {
    return items.reduce((sum, it) => sum + Number(it.lineTotal ?? 0), 0);
  }, [items]);

  const changeQty = async (variantId, nextQty) => {
    if (nextQty < 1) return;

    try {
      await api.cart.updateQty(variantId, { qty: nextQty });
      load();
    } catch (e) {
      const msg = e?.response?.data;
      alert(typeof msg === "string" ? msg : "Failed to update quantity");
    }
  };

  const removeItem = async (variantId) => {
    try {
      await api.cart.remove(variantId);
      load();
    } catch (e) {
      const msg = e?.response?.data;
      alert(typeof msg === "string" ? msg : "Failed to remove item");
    }
  };

  const clear = async () => {
    try {
      await api.cart.clear();
      load();
    } catch {
      alert("Failed to clear cart");
    }
  };

  const checkout = async () => {
    try {
      await api.orders.checkout({}); // ако backend иска адрес/phone - ще го добавим
      alert("Order created!");
      navigate("/products");
    } catch (e) {
      const msg = e?.response?.data;
      alert(typeof msg === "string" ? msg : "Checkout failed");
    }
  };

  if (loading) return <div className="cart__info">Loading...</div>;
  if (err) return <div className="cart__error">{err}</div>;

  return (
    <div className="cart">
      <div className="cart__top">
        <h2>Cart</h2>
        <Link className="cart__back" to="/products">← Continue shopping</Link>
      </div>

      {items.length === 0 ? (
        <div className="cart__empty">
          Your cart is empty.
          <div style={{ marginTop: 10 }}>
            <Link to="/products" className="btn btn--primary">Browse products</Link>
          </div>
        </div>
      ) : (
        <>
          <div className="cart__list">
            {items.map((it) => (
              <div className="cartItem" key={it.variantId}>
                <div className="cartItem__imgWrap">
                  {it.imageUrl ? (
                    <img className="cartItem__img" src={it.imageUrl} alt={it.productName} />
                  ) : (
                    <div className="cartItem__imgPlaceholder">No image</div>
                  )}
                </div>

                <div className="cartItem__mid">
                  <div className="cartItem__title">{it.productName}</div>
                  <div className="cartItem__meta">
                    <span className="badge badge--neutral">{it.type}</span>
                    <span className="badge badge--neutral">{it.audience}</span>
                    <span className="badge badge--neutral">Size: {it.size}</span>
                  </div>

                  <div className="cartItem__price">
                    {Number(it.unitPrice).toFixed(2)} €
                  </div>
                </div>

                <div className="cartItem__right">
                  <div className="qty">
                    <button
                      className="qty__btn"
                      onClick={() => changeQty(it.variantId, it.qty - 1)}
                      disabled={it.qty <= 1}
                    >
                      −
                    </button>

                    <span className="qty__val">{it.qty}</span>

                    <button
                      className="qty__btn"
                      onClick={() => changeQty(it.variantId, it.qty + 1)}
                      disabled={it.qty >= it.maxQty} // backend да прати maxQty = stock
                      title={it.qty >= it.maxQty ? "No more stock" : ""}
                    >
                      +
                    </button>
                  </div>

                  <div className="cartItem__lineTotal">
                    {Number(it.lineTotal).toFixed(2)} €
                  </div>

                  <button className="cartItem__remove" onClick={() => removeItem(it.variantId)}>
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="cart__summary">
            <div className="cart__summaryRow">
              <span>Total</span>
              <b>{Number(total).toFixed(2)} €</b>
            </div>

            <div className="cart__summaryActions">
              <button className="btn" onClick={clear}>Clear cart</button>
              <button className="btn btn--primary" onClick={checkout}>
                Checkout
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
