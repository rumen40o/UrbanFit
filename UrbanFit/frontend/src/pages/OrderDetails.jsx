import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";
import "./Css/Orders.css";
import ReviewModal from "./components/ReviewModal";

export default function OrderDetails({ user }) {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [reviewsMap, setReviewsMap] = useState({}); // productId -> reviews[]
  const [reviewModal, setReviewModal] = useState({ open: false, productId: null, existing: null });

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    setErr("");
    const loader = async () => {
      try {
        let res;
        if (user?.role === "ADMIN") {
          res = await api.adminOrders.get(id);
        } else {
          res = await api.orders.get(id);
        }
        const ord = res.data;
        setOrder(ord);
        // load reviews for each product in the order
        try {
          const items = (ord?.items || []);
          const map = {};
          for (const it of items) {
            const r = await api.reviews.byProduct(it.productId);
            map[it.productId] = r.data || [];
          }
          setReviewsMap(map);
        } catch (e) {
          // ignore review loading errors
        }
      } catch (e) {
        setErr("Failed to load order");
      } finally {
        setLoading(false);
      }
    };
    loader();
  }, [id, user]);

  if (loading) return <div className="ordersPage"><div className="muted">Loading…</div></div>;
  if (err) return <div className="ordersPage"><div style={{ color: 'red' }}>{err}</div></div>;
  if (!order) return null;

  return (
    <div className="ordersPage">
      <div className="ordersHeader">
        <h2>Order #{order.id}</h2>
        <div className={`badge badge--${order.status}`}>{order.status}</div>
      </div>

      <div className="small muted">Created: {new Date(order.createdAt).toLocaleString()}</div>
      <div style={{ marginTop: 8 }}>Payment: {order.paymentStatus}</div>
      <div style={{ marginTop: 8 }}>Shipping: {order.shippingName} / {order.shippingPhone}</div>
      <div style={{ marginTop: 4 }}>Address: {order.shippingAddress}</div>

      <h3 style={{ marginTop: 12 }}>Items</h3>
      <div className="orderItems">
        {order.items.map((it) => {
          const reviews = reviewsMap[it.productId] || [];
          const mine = reviews.find((r) => r.userId === user?.id);
          return (
            <div key={it.variantId} className="orderItem">
              <div>
                <div><b>{it.productName}</b></div>
                <div className="small">{it.type} • {it.audience} • Size: {it.size}</div>
                <div className="small muted">{reviews.length} reviews</div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <div style={{ marginBottom: 6 }}>{it.quantity} × {Number(it.unitPrice).toFixed(2)} €</div>
                <div><b>{Number(it.lineTotal).toFixed(2)} €</b></div>
                {order.status === 'DELIVERED' && (
                  <div style={{ marginTop: 8 }}>
                    {!mine ? (
                      <button className="btn" onClick={async () => {
                        // open review modal for this product
                        const r = await api.reviews.byProduct(it.productId).catch(() => ({ data: [] }));
                        const existing = (r.data || []).find((x) => x.userId === user?.id) || null;
                        setReviewModal({ open: true, productId: it.productId, existing });
                      }}>Leave review</button>
                    ) : (
                      <span className="small">You reviewed ({mine.rating})</span>
                    )}
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>

      <div style={{ marginTop: 12 }}>
        <b>Total: {Number(order.total).toFixed(2)} €</b>
      </div>

      <ReviewModal open={reviewModal.open} productId={reviewModal.productId} existing={reviewModal.existing} onClose={() => setReviewModal({ open: false, productId: null, existing: null })} onSaved={() => {
        // reload reviews for that product
        const pid = reviewModal.productId;
        api.reviews.byProduct(pid).then((res) => setReviewsMap((m) => ({ ...m, [pid]: res.data || [] }))).catch(()=>{});
      }} />
    </div>
  );
}
