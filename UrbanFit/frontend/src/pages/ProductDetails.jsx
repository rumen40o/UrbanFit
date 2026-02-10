import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/api";
import "./Css/ProductDetails.css";
import "./Css/Orders.css";
import ReviewModal from "./components/ReviewModal";

export default function ProductDetails({ user }) {
  const { id } = useParams();

  const [p, setP] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  const [selectedVariantId, setSelectedVariantId] = useState("");
  const [reviews, setReviews] = useState([]);
  const [avgRating, setAvgRating] = useState(null);
  const [reviewModalOpen, setReviewModalOpen] = useState(false);
  const [myReview, setMyReview] = useState(null);

  const selectedVariant = useMemo(() => {
    if (!p?.variants?.length) return null;
    return p.variants.find((v) => String(v.id) === String(selectedVariantId)) || null;
  }, [p, selectedVariantId]);

  useEffect(() => {
    setLoading(true);
    setErr("");

    api.products
      .get(id)
      .then((res) => {
        setP(res.data);

        // default selection: first in-stock else first
        const variants = res.data?.variants || [];
        const inStock = variants.find((v) => (v.stockQty ?? 0) > 0);
        const first = variants[0];
        setSelectedVariantId(String(inStock?.id ?? first?.id ?? ""));
      })
      .catch((e) => {
        const msg = e?.response?.data;
        setErr(typeof msg === "string" ? msg : "Failed to load product");
        setP(null);
      })
      .finally(() => setLoading(false));
    // load reviews
    api.reviews.byProduct(id)
      .then((r) => {
        const list = r.data || [];
        setReviews(list);
        if (list.length > 0) {
          const avg = list.reduce((a, b) => a + (b.rating || 0), 0) / list.length;
          setAvgRating(avg);
        } else setAvgRating(null);
        const mine = list.find((x) => x.userId === user?.id) || null;
        setMyReview(mine);
      })
      .catch(() => {});
  }, [id]);

  const addToCart = async () => {
    if (!selectedVariantId) return;
    try {
      await api.cart.add({ variantId: Number(selectedVariantId), qty: 1 });
      alert("Added to cart");
    } catch (e) {
      const msg = e?.response?.data;
      alert(typeof msg === "string" ? msg : "Failed to add to cart");
    }
  };

  if (loading) return <div className="pd__info">Loading...</div>;
  if (err) return <div className="pd__error">{err}</div>;
  if (!p) return <div className="pd__info">Not found</div>;

  return (
    <div className="pd">
      <div className="pd__top">
        <Link to="/products" className="pd__back">
          ← Back to products
        </Link>
      </div>

      <div className="pd__grid">
        <div className="pd__imgWrap">
          {p.imageUrl ? (
            <img className="pd__img" src={p.imageUrl} alt={p.name} />
          ) : (
            <div className="pd__imgPlaceholder">No image</div>
          )}
        </div>

        <div className="pd__content">
          <h2 className="pd__title">{p.name}</h2>

          <div className="pd__metaRow">
            <span className="pd__pill">{p.type}</span>
            <span className="pd__pill">{p.audience}</span>
          </div>

          <div className="pd__price">{Number(p.price).toFixed(2)} €</div>

          {p.description && <p className="pd__desc">{p.description}</p>}

          <div className="pd__panel">
            <div className="pd__row">
              <label className="pd__label">Size</label>

              <div className="pd__sizes">
                {p.variants?.length ? (
                  p.variants.map((v) => {
                    const disabled = (v.stockQty ?? 0) <= 0;
                    const selected = String(v.id) === String(selectedVariantId);

                    return (
                      <button
                        key={v.id}
                        type="button"
                        className={`pd__sizeBtn ${selected ? "is-selected" : ""}`}
                        disabled={disabled}
                        onClick={() => setSelectedVariantId(String(v.id))}
                        title={disabled ? "Out of stock" : `In stock: ${v.stockQty}`}
                      >
                        {v.size}
                      </button>
                    );
                  })
                ) : (
                  <span className="pd__muted">No sizes</span>
                )}
              </div>
            </div>

            <div className="pd__row">
              <div className="pd__stock">
                {selectedVariant ? (
                  selectedVariant.stockQty > 0 ? (
                    <span className="pd__ok">In stock: {selectedVariant.stockQty}</span>
                  ) : (
                    <span className="pd__bad">Out of stock</span>
                  )
                ) : (
                  <span className="pd__muted">Choose a size</span>
                )}
              </div>

              <button
                className="pd__btn"
                onClick={addToCart}
                disabled={!selectedVariant || selectedVariant.stockQty <= 0}
              >
                {selectedVariant ? `Add size ${selectedVariant.size}` : "Add to cart"}
              </button>
            </div>
          </div>

          <div style={{ marginTop: 18 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div>
                <strong>Rating</strong>
                <div className="small muted">{avgRating ? avgRating.toFixed(1) : 'No rating yet'}</div>
              </div>
            </div>

            <div style={{ marginTop: 12 }}>
              {reviews.length === 0 ? (
                <div className="muted">No reviews yet.</div>
              ) : (
                <div style={{ display: 'grid', gap: 10 }}>
                  {reviews.map((r) => (
                    <div key={r.id} style={{ border: '1px solid #f1f5f9', padding: 10, borderRadius: 8 }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <div><b>{r.username || 'User'}</b></div>
                        <div className={`badge badge--${r.rating >= 4 ? 'PAID' : r.rating >= 3 ? 'SHIPPED' : 'PENDING'}`}>{r.rating}★</div>
                      </div>
                      {r.comment && <div className="small muted" style={{ marginTop: 6 }}>{r.comment}</div>}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          
        </div>
      </div>

      {/* Reviews section - добавяме след това ако искаш */}
    </div>
  );
        <ReviewModal open={reviewModalOpen} productId={id} existing={myReview} onClose={() => setReviewModalOpen(false)} onSaved={() => {
          api.reviews.byProduct(id).then((r) => {
            const list = r.data || [];
            setReviews(list);
            if (list.length > 0) {
              const avg = list.reduce((a, b) => a + (b.rating || 0), 0) / list.length;
              setAvgRating(avg);
            } else setAvgRating(null);
            const mine = list.find((x) => x.userId === user?.id) || null;
            setMyReview(mine);
          }).catch(()=>{});
        }} />
}
