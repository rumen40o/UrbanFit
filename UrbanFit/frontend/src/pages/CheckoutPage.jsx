import { useMemo, useState, useEffect } from "react";
import api from "../api/api";
import { useNavigate } from "react-router-dom";
import AddressPickerMap from "./components/AddressPickerMap";
import CardPaymentModal from "./components/CardPaymentModal";
import "./Css/CheckoutPage.css";

export default function CheckoutPage() {
  const [cart, setCart] = useState(null);
  const [loadingCart, setLoadingCart] = useState(true);

  const [shippingName, setShippingName] = useState("");
  const [shippingPhone, setShippingPhone] = useState("");
  const [shippingAddress, setShippingAddress] = useState("");

  const [latLng, setLatLng] = useState(null);

  // payment
  const [cardModalOpen, setCardModalOpen] = useState(false);
  const [payWithCard, setPayWithCard] = useState(false);
  const [cardConfirmed, setCardConfirmed] = useState(false); // ✅ true само след "Плати" в модала

  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  useEffect(() => {
    (async () => {
      try {
        setLoadingCart(true);
        const res = await api.cart.get();
        setCart(res.data);
      } catch (e) {
        setMsg("Не мога да заредя количката.");
      } finally {
        setLoadingCart(false);
      }
    })();
  }, []);

  const subtotal = useMemo(() => {
    if (!cart?.items?.length) return 0;

    return cart.items.reduce((sum, it) => {
      const qty = Number(it.quantity ?? it.qty ?? 1);
      const price = Number(
        it.unitPrice ?? it.price ?? it.variantPrice ?? it.product?.price ?? 0
      );
      return sum + qty * price;
    }, 0);
  }, [cart]);

  const shippingFee = useMemo(() => (subtotal >= 100 ? 0 : 3), [subtotal]);
  const total = useMemo(() => subtotal + shippingFee, [subtotal, shippingFee]);

  const canSubmit = useMemo(() => {
    const base =
      !loading &&
      !!cart?.items?.length &&
      shippingName.trim() &&
      shippingPhone.trim() &&
      shippingAddress.trim();

    // ✅ ако е избрано CARD, искаме да е потвърдено с "Плати"
    if (payWithCard) return base && cardConfirmed;
    return base;
  }, [
    loading,
    cart,
    shippingName,
    shippingPhone,
    shippingAddress,
    payWithCard,
    cardConfirmed,
  ]);

  async function reloadCartSilently() {
    try {
      const res = await api.cart.get();
      setCart(res.data);
    } catch {
      // ignore
    }
  }

  async function onSubmit(e) {
    e.preventDefault();
    setMsg("");

    if (!cart?.items?.length) return setMsg("Количката е празна.");
    if (!shippingName.trim()) return setMsg("Въведи име за доставка.");
    if (!shippingPhone.trim()) return setMsg("Въведи телефон.");
    if (!shippingAddress.trim()) return setMsg("Въведи адрес.");
    if (payWithCard && !cardConfirmed) return setMsg("Потвърди плащането с карта.");

    const payload = {
      shippingName: shippingName.trim(),
      shippingPhone: shippingPhone.trim(),
      shippingAddress: shippingAddress.trim(),
      paidWithCard: payWithCard && cardConfirmed, // ✅ PAID само ако реално е натиснато "Плати"
    };

    try {
      setLoading(true);

      await api.orders.checkout(payload);

      await api.cart.clear().catch(() => {});
      await reloadCartSilently();

      setShippingName("");
      setShippingPhone("");
      setShippingAddress("");
      setLatLng(null);

      setPayWithCard(false);
      setCardConfirmed(false);
      setCardModalOpen(false);

      setMsg("Поръчката е приета ✅");

      setTimeout(() => {
        navigate("/");
      }, 800);
    } catch (err) {
      const m =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        (typeof err?.response?.data === "string" ? err.response.data : null) ||
        err?.message ||
        "Неуспешна поръчка.";
      setMsg(m);
    } finally {
      setLoading(false);
    }
  }

  function selectCOD() {
    setPayWithCard(false);
    setCardConfirmed(false);
    setCardModalOpen(false);
  }

  function selectCard() {
    setPayWithCard(true);
    setCardConfirmed(false); // докато не натисне "Плати"
    setCardModalOpen(true);
  }

  return (
    <div className="checkout">
      <h1 className="checkout__title">Checkout</h1>

      {msg && <div className="checkout__msg">{msg}</div>}

      <div className="checkout__grid">
        <form className="checkout__form" onSubmit={onSubmit}>
          <h2 className="checkout__sectionTitle">Доставка</h2>

          <label className="checkout__label">
            Име и фамилия
            <input
              className="checkout__input"
              value={shippingName}
              onChange={(e) => setShippingName(e.target.value)}
              placeholder="Пример: Иван Иванов"
            />
          </label>

          <label className="checkout__label">
            Телефон
            <input
              className="checkout__input"
              value={shippingPhone}
              onChange={(e) => setShippingPhone(e.target.value)}
              placeholder="Пример: 0888 123 456"
            />
          </label>

          <AddressPickerMap
            valueLatLng={latLng}
            onChangeLatLng={setLatLng}
            onResolvedAddress={(addr) => setShippingAddress(addr)}
          />

          <label className="checkout__label">
            Адрес
            <textarea
              className="checkout__textarea"
              value={shippingAddress}
              onChange={(e) => setShippingAddress(e.target.value)}
              placeholder="Град, квартал, улица, №, вход, етаж, ап."
              rows={3}
            />
          </label>

          <div className="checkout__pay">
            <div className="checkout__payTitle">Плащане</div>

            <label className="checkout__radio">
              <input
                type="radio"
                name="payment"
                value="COD"
                checked={!payWithCard}
                onChange={selectCOD}
              />
              Наложен платеж
            </label>

            <label className="checkout__radio">
              <input
                type="radio"
                name="payment"
                value="CARD"
                checked={payWithCard}
                onChange={selectCard}
              />
              Карта (симулация)
              {payWithCard && (
                <span className="checkout__cardBadge">
                  {cardConfirmed ? "✓ потвърдена" : "непотвърдена"}
                </span>
              )}
            </label>

            {payWithCard && !cardConfirmed && (
              <button
                type="button"
                className="checkout__cardOpenBtn"
                onClick={() => setCardModalOpen(true)}
              >
                Въведи данни за картата
              </button>
            )}
          </div>

          <button className="checkout__btn" disabled={!canSubmit} type="submit">
            {loading ? "Пращам..." : "Направи поръчка"}
          </button>
        </form>

        <aside className="checkout__summary">
          <h2 className="checkout__sectionTitle">Обобщение</h2>

          {loadingCart ? (
            <div className="checkout__empty">Зареждам количката…</div>
          ) : !cart?.items?.length ? (
            <div className="checkout__empty">Количката е празна.</div>
          ) : (
            <div className="checkout__items">
              {cart.items.map((it, idx) => {
                const qty = Number(it.quantity ?? it.qty ?? 1);
                const price = Number(
                  it.unitPrice ?? it.price ?? it.variantPrice ?? it.product?.price ?? 0
                );

                const title =
                  it.name ?? it.productName ?? it.product?.name ?? `Item ${idx + 1}`;

                return (
                  <div className="checkout__item" key={it.id ?? it.variantId ?? idx}>
                    <div>
                      <div className="checkout__itemName">{title}</div>
                      <div className="checkout__itemMeta">
                        {qty} × {price.toFixed(2)} €
                      </div>
                    </div>
                    <div className="checkout__itemPrice">
                      {(qty * price).toFixed(2)} €
                    </div>
                  </div>
                );
              })}
            </div>
          )}

          <div className="checkout__totals">
            <div className="checkout__row">
              <span>Междинна сума</span>
              <span>{subtotal.toFixed(2)} €</span>
            </div>
            <div className="checkout__row">
              <span>Доставка</span>
              <span>{shippingFee.toFixed(2)} €</span>
            </div>
            <div className="checkout__row checkout__row--strong">
              <span>Общо</span>
              <span>{total.toFixed(2)} €</span>
            </div>
          </div>
        </aside>
      </div>

      <CardPaymentModal
        open={cardModalOpen}
        amountText={`${total.toFixed(2)} €`}
        onClose={() => {
          setCardModalOpen(false);
          // ако затвори без плащане -> правим го COD, за да е логично
          setPayWithCard(false);
          setCardConfirmed(false);
        }}
        onConfirm={() => {
          setCardModalOpen(false);
          setPayWithCard(true);
          setCardConfirmed(true); // ✅ това е “симулираното успешно плащане”
        }}
      />
    </div>
  );
}
