import { useEffect, useMemo, useState } from "react";
import api from "../../api/api";
import "./css/EditProductModal.css";

export default function EditProductModal({ open, productId, onClose, onSaved }) {
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [err, setErr] = useState("");

  const [form, setForm] = useState({
    name: "",
    price: 0,
    type: "HOODIE",
    audience: "MEN",
    active: true,
    imageUrl: "",
    description: "",
  });

  // ✅ variants state: [{ id, size, stockQty }]
  const [variants, setVariants] = useState([]);
  const hasVariants = useMemo(() => variants && variants.length > 0, [variants]);

  useEffect(() => {
    if (!open || !productId) return;

    (async () => {
      setErr("");
      setLoading(true);
      try {
        const res = await api.products.get(productId);
        const p = res.data;

        setForm({
          name: p.name ?? "",
          price: Number(p.price ?? 0),
          type: p.type ?? "HOODIE",
          audience: p.audience ?? "MEN",
          active: Boolean(p.active ?? true),
          imageUrl: p.imageUrl ?? "",
          description: p.description ?? "",
        });

        // ✅ Read variants from product response
        const v = Array.isArray(p.variants) ? p.variants : [];

        setVariants(
          (v || []).map((x) => ({
            id: x.id,
            size: x.size ?? "",
            stockQty: Number(x.stockQty ?? 0),
          }))
        );
      } catch (e) {
        setErr("Не мога да заредя продукта.");
        setVariants([]);
      } finally {
        setLoading(false);
      }
    })();
  }, [open, productId]);

  function setField(k, v) {
    setForm((prev) => ({ ...prev, [k]: v }));
  }

  function setVariantStock(variantIndex, nextStock) {
    // Allow empty string for user input, convert to number for validation on save
    const val = nextStock === "" ? "" : Math.max(0, Number(nextStock || 0));
    setVariants((prev) =>
      prev.map((x, idx) => (idx === variantIndex ? { ...x, stockQty: val } : x))
    );
  }

  function setVariantSize(variantIndex, newSize) {
    setVariants((prev) =>
      prev.map((x, idx) => (idx === variantIndex ? { ...x, size: newSize } : x))
    );
  }

  function addNewVariant() {
    const newVariant = {
      id: null,
      size: "",
      stockQty: 0,
    };
    setVariants((prev) => [...prev, newVariant]);
  }

  function removeVariant(variantIndex) {
    setVariants((prev) => prev.filter((x, idx) => idx !== variantIndex));
  }

  async function save() {
    setErr("");

    if (!form.name.trim()) return setErr("Името е задължително.");
    if (Number.isNaN(Number(form.price)) || Number(form.price) < 0)
      return setErr("Невалидна цена.");

    // variants validation
    if (variants.length === 0) return setErr("Трябва да има поне един размер.");
    for (const v of variants) {
      if (!v.size || v.size.trim() === "") return setErr("Размерът е задължителен.");
      const stockNum = Number(v.stockQty);
      if (Number.isNaN(stockNum) || stockNum < 0)
        return setErr("Невалидна бройка за размер.");
    }

    try {
      setSaving(true);

      const payload = {
        name: form.name,
        price: Number(form.price),
        type: form.type,
        audience: form.audience,
        active: form.active,
        imageUrl: form.imageUrl,
        description: form.description,
        variants: variants.map(v => ({
          id: v.id,
          size: v.size,
          stockQty: v.stockQty
        }))
      };

      await api.products.update(productId, payload);

      onSaved?.();
      onClose?.();
    } catch (e) {
      const m =
        e?.response?.data?.message ||
        e?.response?.data ||
        "Неуспешен update.";
      setErr(typeof m === "string" ? m : "Неуспешен update.");
    } finally {
      setSaving(false);
    }
  }

  if (!open) return null;

  return (
    <div className="epm__backdrop" onMouseDown={onClose}>
      <div className="epm__panel" onMouseDown={(e) => e.stopPropagation()}>
        <div className="epm__top">
          <div>
            <div className="epm__title">Edit product</div>
            <div className="epm__sub">ID: {productId}</div>
          </div>
          <button className="epm__x" onClick={onClose}>✕</button>
        </div>

        {loading ? (
          <div className="epm__info">Loading…</div>
        ) : (
          <>
            {err && <div className="epm__err">{err}</div>}

            <div className="epm__grid">
              <label className="epm__field">
                Name
                <input
                  value={form.name}
                  onChange={(e) => setField("name", e.target.value)}
                />
              </label>

              <label className="epm__field">
                Price
                <input
                  type="number"
                  step="0.01"
                  value={form.price}
                  onChange={(e) => setField("price", e.target.value)}
                />
              </label>

              <label className="epm__field">
                Type
                <select
                  value={form.type}
                  onChange={(e) => setField("type", e.target.value)}
                >
                  <option value="HOODIE">HOODIE</option>
                  <option value="TSHIRT">TSHIRT</option>
                  <option value="PANTS">PANTS</option>
                  <option value="SHORTS">SHORTS</option>
                  <option value="SHOES">SHOES</option>
                  <option value="JACKET">JACKET</option>
                  <option value="DRESS">DRESS</option>
                  <option value="ACCESSORIES">ACCESSORIES</option>
                </select>
              </label>

              <label className="epm__field">
                Audience
                <select
                  value={form.audience}
                  onChange={(e) => setField("audience", e.target.value)}
                >
                  <option value="MEN">MEN</option>
                  <option value="WOMEN">WOMEN</option>
                  <option value="KIDS">KIDS</option>
                </select>
              </label>

              <label className="epm__field epm__field--full">
                Image URL
                <input
                  value={form.imageUrl}
                  onChange={(e) => setField("imageUrl", e.target.value)}
                />
              </label>

              <label className="epm__field epm__field--full">
                Description
                <textarea
                  rows={3}
                  value={form.description}
                  onChange={(e) => setField("description", e.target.value)}
                />
              </label>

              <label className="epm__check epm__field--full">
                <input
                  type="checkbox"
                  checked={form.active}
                  onChange={(e) => setField("active", e.target.checked)}
                />
                Active
              </label>
            </div>

            {/* ✅ STOCK BY SIZE */}
            <div className="epm__sizesSection">
              <div className="epm__sizeHeader">
                <h3 className="epm__h3">Stock by size</h3>
                <button
                  className="epm__addSizeBtn"
                  type="button"
                  onClick={addNewVariant}
                >
                  + Add Size
                </button>
              </div>

              {variants.length === 0 ? (
                <div className="epm__info epm__info--pad">
                  No sizes added yet. Click "Add Size" to add one.
                </div>
              ) : (
                <div className="epm__variants">
                  {variants.map((v, idx) => (
                    <div className="epm__variantRow" key={v.id || `new-${idx}`}>
                      <input
                        className="epm__variantSizeInput"
                        type="text"
                        placeholder="Size (S, M, L, XL...)"
                        value={v.size}
                        onChange={(e) => setVariantSize(idx, e.target.value)}
                      />
                      <input
                        className="epm__variantInput"
                        type="number"
                        min="0"
                        placeholder="Stock"
                        value={v.stockQty}
                        onChange={(e) => setVariantStock(idx, e.target.value)}
                      />
                      <button
                        className="epm__removeBtn"
                        type="button"
                        onClick={() => removeVariant(idx)}
                      >
                        ✕
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="epm__actions">
              <button className="epm__btn" onClick={onClose}>Cancel</button>
              <button
                className="epm__btn epm__btn--primary"
                onClick={save}
                disabled={saving}
              >
                {saving ? "Saving…" : "Save"}
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
