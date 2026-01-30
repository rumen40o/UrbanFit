import { useEffect, useState } from "react";
import api from "../../api/api";
import { CLOTHING_TYPES, AUDIENCES } from "./productEnums";
import "./css/Modal.css";

export default function AddProductModal({ open, onClose, onCreated }) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [stockQty, setStockQty] = useState(0);
  const [active, setActive] = useState(true);

  // NEW
  const [type, setType] = useState("");
  const [audience, setAudience] = useState("");

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");

  useEffect(() => {
    if (!open) return;

    setName("");
    setDescription("");
    setPrice("");
    setImageUrl("");
    setStockQty(0);
    setActive(true);

    // NEW resets
    setType("");
    setAudience("");

    setErr("");
    setLoading(false);
  }, [open]);

  const submit = async (e) => {
    e.preventDefault();
    setErr("");

    const parsedPrice = Number(price);

    if (!name.trim()) return setErr("Name is required");
    if (Number.isNaN(parsedPrice) || parsedPrice < 0) return setErr("Price must be a valid number >= 0");
    if (Number(stockQty) < 0) return setErr("Stock must be >= 0");

    // NEW required
    if (!type) return setErr("Type is required");
    if (!audience) return setErr("Audience is required");

    setLoading(true);
    try {
      await api.products.create({
        name: name.trim(),
        description: description || null,
        price: parsedPrice,
        imageUrl: imageUrl || null,
        stockQty: Number(stockQty) || 0,
        active,
        type,       // enum string e.g. "TSHIRT"
        audience,   // enum string e.g. "MEN"
      });

      onCreated?.();
      onClose();
    } catch (e2) {
      const msg = e2?.response?.data;
      setErr(typeof msg === "string" ? msg : "Failed to create product");
    } finally {
      setLoading(false);
    }
  };

  if (!open) return null;

  return (
    <div className="modalOverlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal__header">
          <h3>Add Product</h3>
          <button className="modal__close" onClick={onClose}>✕</button>
        </div>

        <form className="modal__form" onSubmit={submit}>
          <label className="modal__label">
            Name *
            <input
              className="modal__input"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </label>

          <label className="modal__label">
            Description
            <textarea
              className="modal__textarea"
              rows={4}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </label>

          <div className="modal__row">
            <label className="modal__label">
              Price (лв.) *
              <input
                className="modal__input"
                type="number"
                step="0.01"
                min="0"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
              />
            </label>

            <label className="modal__label">
              Stock
              <input
                className="modal__input"
                type="number"
                min="0"
                value={stockQty}
                onChange={(e) => setStockQty(e.target.value)}
              />
            </label>
          </div>

          {/* NEW: Type + Audience */}
          <div className="modal__row">
            <label className="modal__label">
              Type *
              <select
                className="modal__select"
                value={type}
                onChange={(e) => setType(e.target.value)}
              >
                <option value="">Select type</option>
                {CLOTHING_TYPES.map((t) => (
                  <option key={t} value={t}>{t}</option>
                ))}
              </select>
            </label>

            <label className="modal__label">
              Audience *
              <select
                className="modal__select"
                value={audience}
                onChange={(e) => setAudience(e.target.value)}
              >
                <option value="">Select audience</option>
                {AUDIENCES.map((a) => (
                  <option key={a} value={a}>{a}</option>
                ))}
              </select>
            </label>
          </div>

          <label className="modal__label">
            Image URL
            <input
              className="modal__input"
              value={imageUrl}
              onChange={(e) => setImageUrl(e.target.value)}
            />
          </label>

          <label className="modal__checkbox">
            <input
              type="checkbox"
              checked={active}
              onChange={(e) => setActive(e.target.checked)}
            />
            Active (visible in public list)
          </label>

          {err && <div className="modal__error">{err}</div>}

          <div className="modal__actions">
            <button type="button" className="btn" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn--primary" disabled={loading}>
              {loading ? "Saving..." : "Add"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
