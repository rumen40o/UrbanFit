import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import "./Css/Orders.css";

const ORDER_STATUSES = ["PENDING", "PAID", "SHIPPED", "DELIVERED", "CANCELLED"];

export default function AdminOrders({ user }) {
  const [pageData, setPageData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [page, setPage] = useState(0);

  useEffect(() => {
    if (!user || user.role !== "ADMIN") return;
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user, page]);

  const load = async () => {
    setLoading(true);
    setErr("");
    try {
      const res = await api.adminOrders.list({ page, size: 20 });
      setPageData(res.data);
    } catch (e) {
      setErr("Failed to load orders");
    } finally {
      setLoading(false);
    }
  };

  const changeStatus = async (id, status) => {
    try {
      await api.adminOrders.updateStatus(id, { status });
      load();
    } catch (e) {
      alert("Failed to update status");
    }
  };

  if (!user || user.role !== "ADMIN") {
    return <div style={{ padding: 16 }}>Access denied.</div>;
  }

  return (
    <div className="ordersPage">
      <div className="ordersHeader">
        <h2>Admin Orders</h2>
      </div>
      {loading && <div className="muted">Loading…</div>}
      {err && <div style={{ color: 'red' }}>{err}</div>}

      {pageData && (
        <>
          <table className="ordersTable">
            <thead>
              <tr>
                <th>ID</th>
                <th>User</th>
                <th>Created</th>
                <th>Total</th>
                <th>Status</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {pageData.content.map((o) => (
                <tr key={o.id}>
                  <td>{o.id}</td>
                  <td className="small">{o.shippingName || o.userEmail || '-'}</td>
                  <td className="small">{new Date(o.createdAt).toLocaleString()}</td>
                  <td>{Number(o.total).toFixed(2)} €</td>
                  <td>
                    <select className="statusSelect" value={o.status} onChange={(e) => changeStatus(o.id, e.target.value)}>
                      {ORDER_STATUSES.map((s) => (
                        <option key={s} value={s}>{s}</option>
                      ))}
                    </select>
                  </td>
                  <td>
                    <Link to={`/orders/${o.id}`}>View</Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="ordersPager">
            <button className="btn" disabled={page <= 0} onClick={() => setPage((p) => Math.max(0, p - 1))}>Prev</button>
            <span className="small">Page {pageData.number + 1} / {pageData.totalPages}</span>
            <button className="btn" disabled={pageData.number + 1 >= pageData.totalPages} onClick={() => setPage((p) => p + 1)}>Next</button>
          </div>
        </>
      )}
    </div>
  );
}
