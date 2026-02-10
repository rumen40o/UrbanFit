import { useEffect, useMemo, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import AddProductModal from "./components/AddProductModal";
import EditProductModal from "./components/EditProductModal";
import ConfirmDialog from "./components/ConfirmDialog";
import api from "../api/api";
import { CLOTHING_TYPES, AUDIENCES } from "./components/productEnums";
import "./Css/Products.css";

export default function Products({ user }) {
  const location = useLocation();
  const navigate = useNavigate();

  const [addOpen, setAddOpen] = useState(false);

  // ✅ admin manage/delete/edit state
  const [manageMode, setManageMode] = useState(null); // null, "edit", or "delete"
  const [manageDropdownOpen, setManageDropdownOpen] = useState(false);
  const [selected, setSelected] = useState(() => new Set());
  const [confirmOpen, setConfirmOpen] = useState(false);

  const [editOpen, setEditOpen] = useState(false);
  const [editId, setEditId] = useState(null);

  // query params
  const params = useMemo(() => new URLSearchParams(location.search), [location.search]);
  const qParam = params.get("q") || "";
  const typeParam = params.get("type") || "";
  const audienceParam = params.get("audience") || "";
  const sortParam = params.get("sort") || "createdAt,desc";
  const pageParam = parseInt(params.get("page") || "0", 10);

  // page state
  const [pageData, setPageData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");

  // controls from URL
  const [type, setType] = useState(typeParam);
  const [audience, setAudience] = useState(audienceParam);
  const [sort, setSort] = useState(sortParam);

  useEffect(() => {
    setType(typeParam);
    setAudience(audienceParam);
    setSort(sortParam);
  }, [typeParam, audienceParam, sortParam]);

  const setUrlParams = (next) => {
    const p = new URLSearchParams(location.search);
    Object.entries(next).forEach(([k, v]) => {
      if (v === null || v === undefined || v === "") p.delete(k);
      else p.set(k, String(v));
    });
    navigate(`/products?${p.toString()}`);
  };

  const buildRequestParams = () => ({
    page: isNaN(pageParam) ? 0 : pageParam,
    size: 12,
    sort: sortParam,
    q: qParam || undefined,
    type: typeParam || undefined,
    audience: audienceParam || undefined,
  });

  // load products
  useEffect(() => {
    setLoading(true);
    setErr("");
    api.products
      .list(buildRequestParams())
      .then((res) => setPageData(res.data))
      .catch((e) => {
        const msg = e?.response?.data;
        setErr(typeof msg === "string" ? msg : "Failed to load products");
        setPageData(null);
      })
      .finally(() => setLoading(false));
  }, [qParam, typeParam, audienceParam, sortParam, pageParam]);

  const refresh = () => {
    setLoading(true);
    setErr("");
    api.products
      .list(buildRequestParams())
      .then((res) => setPageData(res.data))
      .catch(() => setErr("Failed to load products"))
      .finally(() => setLoading(false));
  };

  const onTypeChange = (e) => {
    const val = e.target.value;
    setType(val);
    setUrlParams({ type: val || "", page: 0 });
  };

  const onAudienceChange = (e) => {
    const val = e.target.value;
    setAudience(val);
    setUrlParams({ audience: val || "", page: 0 });
  };

  const onSortChange = (e) => {
    const val = e.target.value;
    setSort(val);
    setUrlParams({ sort: val, page: 0 });
  };

  const goPrev = () => setUrlParams({ page: Math.max(pageParam - 1, 0) });
  const goNext = () => {
    if (!pageData) return;
    setUrlParams({ page: Math.min(pageParam + 1, pageData.totalPages - 1) });
  };

  // ✅ selection helpers
  const toggleSelect = (id) => {
    setSelected((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const clearSelection = () => setSelected(new Set());

  const openEdit = (id) => {
    setEditId(id);
    setEditOpen(true);
  };

  const closeEdit = () => {
    setEditOpen(false);
    setEditId(null);
  };

  const editSelected = async () => {
    const ids = Array.from(selected);
    if (ids.length === 0) return;
    if (ids.length === 1) {
      openEdit(ids[0]);
      setManageMode(null);
      clearSelection();
    } else {
      alert("Please select only one product to edit.");
    }
  };

  const deleteSelected = async () => {
    const ids = Array.from(selected);
    if (ids.length === 0) return;

    try {
      await Promise.all(ids.map((id) => api.products.remove(id)));
      setConfirmOpen(false);
      clearSelection();
      setManageMode(null);
      refresh();
    } catch (e) {
      alert("Неуспешно изтриване.");
    }
  };

  const isAdmin = user?.role === "ADMIN";

  return (
    <div className="products">
      <div className="products__header">
        <div className="products__headerRow">
          <h2>Products</h2>

          {/* ✅ admin toolbar */}
          {isAdmin && (
            <div className="prodAdminBar">
              <div className="prodAdminBar__dropdown">
                <button
                  className="btn btn--primary"
                  onClick={() => {
                    if (manageMode) {
                      setManageMode(null);
                      setManageDropdownOpen(false);
                      clearSelection();
                    } else {
                      setManageDropdownOpen(!manageDropdownOpen);
                    }
                  }}
                >
                  {manageMode ? "Exit" : "Manage"}
                </button>

                {manageDropdownOpen && !manageMode && (
                  <div className="dropdown__menu">
                    <button
                      className="dropdown__item"
                      onClick={() => {
                        setManageMode("edit");
                        setManageDropdownOpen(false);
                      }}
                    >
                      ✏️ Edit
                    </button>
                    <button
                      className="dropdown__item"
                      onClick={() => {
                        setManageMode("delete");
                        setManageDropdownOpen(false);
                      }}
                    >
                      🗑️ Delete
                    </button>
                  </div>
                )}
              </div>

              {manageMode === "edit" && (
                <>
                  <button
                    className="btn"
                    disabled={selected.size !== 1}
                    onClick={editSelected}
                  >
                    Edit ({selected.size}/1)
                  </button>

                  <button className="btn" disabled={selected.size === 0} onClick={clearSelection}>
                    Clear
                  </button>
                </>
              )}

              {manageMode === "delete" && (
                <>
                  <button
                    className="btn btn--danger"
                    disabled={selected.size === 0}
                    onClick={() => setConfirmOpen(true)}
                  >
                    Delete selected ({selected.size})
                  </button>

                  <button className="btn" disabled={selected.size === 0} onClick={clearSelection}>
                    Clear
                  </button>
                </>
              )}

              <button className="btn btn--primary" onClick={() => setAddOpen(true)}>
                + Add Product
              </button>
            </div>
          )}
        </div>

        <p className="products__subtitle">
          {qParam ? (
            <>
              Search: <b>{qParam}</b>
            </>
          ) : (
            "Browse all products"
          )}
        </p>
      </div>

      <div className="products__controls">
        <div className="products__control">
          <label>Type</label>
          <select value={type} onChange={onTypeChange}>
            <option value="">All</option>
            {CLOTHING_TYPES.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
        </div>

        <div className="products__control">
          <label>Audience</label>
          <select value={audience} onChange={onAudienceChange}>
            <option value="">All</option>
            {AUDIENCES.map((a) => (
              <option key={a} value={a}>
                {a}
              </option>
            ))}
          </select>
        </div>

        <div className="products__control">
          <label>Sort</label>
          <select value={sort} onChange={onSortChange}>
            <option value="createdAt,desc">Newest</option>
            <option value="price,asc">Price: low → high</option>
            <option value="price,desc">Price: high → low</option>
            <option value="name,asc">Name: A → Z</option>
          </select>
        </div>
      </div>

      {loading && <div className="products__info">Loading...</div>}
      {err && <div className="products__error">{err}</div>}

      {!loading && pageData && pageData.content?.length === 0 && (
        <div className="products__info">No products found.</div>
      )}

      {!loading && pageData && pageData.content?.length > 0 && (
        <>
          <div className="products__grid">
            {pageData.content.map((p) => (
              <div
                key={p.id}
                className={`productCard ${manageMode ? "productCard--manage" : ""}`}
                onClick={() => {
                  // ако не сме в manage mode -> влизаме в details
                  if (!manageMode) navigate(`/products/${p.id}`);
                }}
                role="button"
                tabIndex={0}
              >
                {/* ✅ checkbox overlay in edit or delete mode */}
                {isAdmin && manageMode && (
                  <label
                    className="pc__select"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <input
                      type="checkbox"
                      checked={selected.has(p.id)}
                      onChange={() => toggleSelect(p.id)}
                    />
                  </label>
                )}

                <div className="productCard__imgWrap">
                  {p.imageUrl ? (
                    <img className="productCard__img" src={p.imageUrl} alt={p.name} />
                  ) : (
                    <div className="productCard__imgPlaceholder">No image</div>
                  )}
                </div>

                <div className="productCard__body">
                  <div className="productCard__title">{p.name}</div>

                  <div className="productCard__price">
                    {Number(p.price).toFixed(2)} €
                  </div>

                  <div className="productCard__meta">
                    <span className="badge badge--neutral">{p.type}</span>
                    <span className="badge badge--neutral">{p.audience}</span>

                    {/* ✅ keep a Link if you want explicit */}
                    <Link
                      className="badge badge--neutral"
                      to={`/products/${p.id}`}
                      onClick={(e) => e.stopPropagation()}
                    >
                      View details
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="products__pager">
            <button onClick={goPrev} disabled={pageParam <= 0}>
              Prev
            </button>
            <span>
              Page <b>{(pageData.number ?? pageParam) + 1}</b> / <b>{pageData.totalPages}</b>
            </span>
            <button
              onClick={goNext}
              disabled={(pageData.number ?? pageParam) + 1 >= pageData.totalPages}
            >
              Next
            </button>
          </div>
        </>
      )}

      <AddProductModal open={addOpen} onClose={() => setAddOpen(false)} onCreated={refresh} />

      {/* ✅ confirm delete */}
      <ConfirmDialog
        open={confirmOpen}
        title="Delete products"
        message={`Сигурен ли си, че искаш да изтриеш ${selected.size} продукта? Това не може да се върне.`}
        confirmText="Delete"
        cancelText="Cancel"
        danger
        onClose={() => setConfirmOpen(false)}
        onConfirm={deleteSelected}
      />

      {/* ✅ edit modal */}
      <EditProductModal
        open={editOpen}
        productId={editId}
        onClose={closeEdit}
        onSaved={refresh}
      />
    </div>
  );
}
