import { useEffect, useMemo, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import AddProductModal from "./components/AddProductModal";
import api from "../api/api";
import { CLOTHING_TYPES, AUDIENCES } from "./components/productEnums";
import "./Css/Products.css";

export default function Products({ user }) {
  const location = useLocation();
  const navigate = useNavigate();
  const [addOpen, setAddOpen] = useState(false);

  // query params от URL
  const params = useMemo(() => new URLSearchParams(location.search), [location.search]);
  const qParam = params.get("q") || "";
  const typeParam = params.get("type") || "";
  const audienceParam = params.get("audience") || "";
  const sortParam = params.get("sort") || "createdAt,desc";
  const pageParam = parseInt(params.get("page") || "0", 10);

  // local UI state
  const [pageData, setPageData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");

  // controls (от URL)
  const [type, setType] = useState(typeParam);
  const [audience, setAudience] = useState(audienceParam);
  const [sort, setSort] = useState(sortParam);

  // синхронизация ако URL се смени отвън (пример: navbar search)
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

  // load products (on URL change)
  useEffect(() => {
    setLoading(true);
    setErr("");

    const requestParams = {
      page: isNaN(pageParam) ? 0 : pageParam,
      size: 12,
      sort: sortParam,
      q: qParam || undefined,
      type: typeParam || undefined,
      audience: audienceParam || undefined,
    };

    api.products
      .list(requestParams)
      .then((res) => setPageData(res.data))
      .catch((e) => {
        const msg = e?.response?.data;
        setErr(typeof msg === "string" ? msg : "Failed to load products");
        setPageData(null);
      })
      .finally(() => setLoading(false));
  }, [qParam, typeParam, audienceParam, sortParam, pageParam]);

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

  const refresh = () => {
    const requestParams = {
      page: isNaN(pageParam) ? 0 : pageParam,
      size: 12,
      sort: sortParam,
      q: qParam || undefined,
      type: typeParam || undefined,
      audience: audienceParam || undefined,
    };

    setLoading(true);
    setErr("");
    api.products
      .list(requestParams)
      .then((res) => setPageData(res.data))
      .catch(() => setErr("Failed to load products"))
      .finally(() => setLoading(false));
  };

  return (
    <div className="products">
      <div className="products__header">
        <div className="products__headerRow">
          <h2>Products</h2>

          {user?.role === "ADMIN" && (
            <button className="btn btn--primary" onClick={() => setAddOpen(true)}>
              + Add Product
            </button>
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
              <Link key={p.id} to={`/products/${p.id}`} className="productCard">
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
                    <span className="badge badge--neutral">View details</span>
                  </div>
                </div>
              </Link>
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

      <AddProductModal
        open={addOpen}
        onClose={() => setAddOpen(false)}
        onCreated={refresh}
      />
    </div>
  );
}
