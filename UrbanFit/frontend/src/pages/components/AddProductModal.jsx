import { useEffect, useState } from "react";
import api from "../../api/api";
import { CLOTHING_TYPES, AUDIENCES } from "./productEnums";
import "./css/Modal.css";

export default function AddProductModal({ open, onClose, onCreated }) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [active, setActive] = useState(true);

  // enums
  const [type, setType] = useState("");
  const [audience, setAudience] = useState("");

  // variants (sizes)
  const [variants, setVariants] = useState([{ size: "", stockQty: 0 }]);

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");

  useEffect(() => {
    if (!open) return;

    setName("");
    setDescription("");
    setPrice("");
    setImageUrl("");
    setActive(true);

    setType("");
    setAudience("");

    setVariants([{ size: "", stockQty: 0 }]);

    setErr("");
    setLoading(false);
  }, [open]);

  const updateVariant = (idx, patch) => {
    setVariants((prev) =>
      prev.map((v, i) => (i === idx ? { ...v, ...patch } : v))
    );
  };

  const addVariantRow = () => {
    setVariants((prev) => [...prev, { size: "", stockQty: 0 }]);
  };

  const removeVariantRow = (idx) => {
    setVariants((prev) => prev.filter((_, i) => i !== idx));
  };

  const submit = async (e) => {
    e.preventDefault();
    setErr("");

    const parsedPrice = Number(price);

    if (!name.trim()) return setErr("Name is required");
    if (Number.isNaN(parsedPrice) || parsedPrice < 0)
      return setErr("Price must be a valid number >= 0");

    if (!type) return setErr("Type is required");
    if (!audience) return setErr("Audience is required");

    // validate variants
    const cleaned = variants
      .map((v) => ({
        size: (v.size || "").trim(),
        stockQty: Number(v.stockQty),
      }))
      .filter((v) => v.size.length > 0); // махаме празните редове

    if (cleaned.length === 0) return setErr("Add at least 1 size variant");

    for (const v of cleaned) {
      if (!Number.isInteger(v.stockQty) || v.stockQty < 0) {
        return setErr("Each variant stock must be an integer >= 0");
      }
    }

    // no duplicate sizes
    const sizes = cleaned.map((v) => v.size.toUpperCase());
    const unique = new Set(sizes);
    if (unique.size !== sizes.length) {
      return setErr("Duplicate sizes are not allowed");
    }

    setLoading(true);
    try {
      await api.products.create({
        name: name.trim(),
        description: description || null,
        price: parsedPrice,
        imageUrl: imageUrl || null,
        active,
        type,
        audience,
        variants: cleaned, // ✅ това е важното
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
          <button className="modal__close" onClick={onClose} type="button">
            ✕
          </button>
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
              Price (€) *
              <input
                className="modal__input"
                type="number"
                step="0.01"
                min="0"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
              />
            </label>
          </div>

          {/* Type + Audience */}
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
                  <option key={t} value={t}>
                    {t}
                  </option>
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
                  <option key={a} value={a}>
                    {a}
                  </option>
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

          {/* Variants */}
          <div className="modal__variants">
            <div className="modal__variantsTop">
              <div className="modal__label" style={{ fontWeight: 700 }}>
                Sizes *
              </div>
              <button type="button" className="btn" onClick={addVariantRow}>
                + Add size
              </button>
            </div>

            {variants.map((v, idx) => (
              <div className="modal__variantRow" key={idx}>
                <input
                  className="modal__input"
                  placeholder="Size (e.g. S, M, 42)"
                  value={v.size}
                  onChange={(e) => updateVariant(idx, { size: e.target.value })}
                />

                <input
                  className="modal__input"
                  type="number"
                  min="0"
                  step="1"
                  value={v.stockQty}
                  onChange={(e) =>
                    updateVariant(idx, { stockQty: e.target.value })
                  }
                />

                <button
                  type="button"
                  className="btn"
                  onClick={() => removeVariantRow(idx)}
                  disabled={variants.length === 1}
                >
                  Remove
                </button>
              </div>
            ))}
          </div>

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
