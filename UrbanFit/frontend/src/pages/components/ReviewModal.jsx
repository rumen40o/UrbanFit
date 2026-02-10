import React, { useEffect, useState } from "react";
import api from "../../api/api";
import "../Css/Orders.css";

export default function ReviewModal({ open, productId, existing, onClose, onSaved }) {
  const [rating, setRating] = useState(existing?.rating || 5);
  const [comment, setComment] = useState(existing?.comment || "");
  const [saving, setSaving] = useState(false);
  const [err, setErr] = useState("");
  const [hoverRating, setHoverRating] = useState(0); // Removed duplicate declaration

  useEffect(() => {
    if (open) {
      setRating(existing?.rating ?? 0);
      setComment(existing?.comment || "");
      setErr("");
      setHoverRating(0);
    }
  }, [open, existing]);

  if (!open) return null;

  const submit = async () => {
    setErr("");
    if (!productId) return setErr("Missing product");
    if (!rating || rating < 1 || rating > 5) return setErr("Invalid rating");
    try {
      setSaving(true);
      await api.reviews.createOrUpdate({ productId: Number(productId), rating: Number(rating), comment });
      onSaved?.();
      onClose?.();
    } catch (e) {
      const m = e?.response?.data || e?.message || "Failed to save review";
      setErr(typeof m === 'string' ? m : 'Failed to save review');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="epm__backdrop" onMouseDown={onClose}>
      <div className="epm__panel" onMouseDown={(e) => e.stopPropagation()} style={{ minWidth: 420 }}>
        <div style={{ display: 'flex', justifyContent:'space-between', alignItems:'center', padding: '12px 16px' }}>
          <div><strong>Leave review</strong></div>
          <button className="epm__x" onClick={onClose}>✕</button>
        </div>
        <div style={{ padding: 16 }}>
          {err && <div className="epm__err">{err}</div>}
          <div style={{ marginBottom: 8 }}>
            <label className="small">Rating</label>
            <div>
              <div className="ratingStars" style={{ display: 'flex', gap: 8, marginTop: 8 }}>
                {[1,2,3,4,5].map((s) => {
                  const filled = hoverRating ? s <= hoverRating : s <= rating;
                  return (
                    <button
                      key={s}
                      type="button"
                      className={`star ${filled ? 'filled' : ''}`}
                      onMouseEnter={() => setHoverRating(s)}
                      onMouseLeave={() => setHoverRating(0)}
                      onFocus={() => setHoverRating(s)}
                      onBlur={() => setHoverRating(0)}
                      onClick={() => setRating(s)}
                      aria-label={`${s} star${s>1? 's':''}`}
                      style={{
                        background: 'transparent',
                        border: 'none',
                        cursor: 'pointer',
                        padding: 8,
                        fontSize: 32, // Increased font size
                        lineHeight: 1,
                      }}
                    >
                      {filled ? '★' : '☆'}
                    </button>
                  )
                })}
              </div>
            </div>
          </div>

          <div>
            <label className="small">Comment</label>
            <textarea value={comment} onChange={(e) => setComment(e.target.value)} style={{ width: '100%', minHeight: 100, padding: 8, borderRadius: 8, border: '1px solid #e6e9ee' }} />
          </div>

          <div style={{ marginTop: 12, textAlign: 'right' }}>
            <button className="epm__btn" onClick={onClose} style={{ marginRight: 8 }}>Cancel</button>
            <button className="epm__btn epm__btn--primary" onClick={submit} disabled={saving}>{saving ? 'Saving…' : 'Save'}</button>
          </div>
        </div>
      </div>
    </div>
  );
}
