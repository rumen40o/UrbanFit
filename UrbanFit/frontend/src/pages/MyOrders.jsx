import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import "./Css/Orders.css";

export default function MyOrders({ user }) {
  const [orders, setOrders] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!user) return;
    setLoading(true);
    api.orders.my()
      .then((res) => setOrders(res.data))
      .catch(() => setOrders([]))
      .finally(() => setLoading(false));
  }, [user]);

  if (!user) return <div style={{ padding: 16 }}>Please login to see your orders.</div>;

  return (
    <div className="ordersPage">
      <div className="ordersHeader">
        <h2>My Orders</h2>
      </div>

      {loading && <div className="muted">Loading…</div>}
      {!loading && orders && orders.length === 0 && <div className="muted">No orders found.</div>}

      {!loading && orders && orders.length > 0 && (
        <div>
          {orders.map((o) => (
            <div className="orderCard" key={o.id}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div><b>Order #{o.id}</b></div>
                  <div className="small muted">Created: {new Date(o.createdAt).toLocaleString()}</div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div className="small">Total</div>
                  <div><b>{Number(o.total).toFixed(2)} €</b></div>
                  <div style={{ marginTop: 6 }} className={`badge badge--${o.status}`}>{o.status}</div>
                </div>
              </div>
              <div style={{ marginTop: 10 }}>
                <Link to={`/orders/${o.id}`}>View details</Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
