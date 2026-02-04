import { useEffect, useMemo, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import "./css/CardPaymentModal.css";

function onlyDigits(s) {
  return (s || "").replace(/\D/g, "");
}

function formatCardNumber(v) {
  const d = onlyDigits(v).slice(0, 16);
  return d.replace(/(.{4})/g, "$1 ").trim();
}

function formatExpiry(v) {
  const d = onlyDigits(v).slice(0, 4);
  if (d.length <= 2) return d;
  return `${d.slice(0, 2)}/${d.slice(2)}`;
}

function luhnCheck(num) {
  const s = onlyDigits(num);
  if (s.length < 13) return false;
  let sum = 0;
  let dbl = false;
  for (let i = s.length - 1; i >= 0; i--) {
    let digit = s.charCodeAt(i) - 48;
    if (dbl) {
      digit *= 2;
      if (digit > 9) digit -= 9;
    }
    sum += digit;
    dbl = !dbl;
  }
  return sum % 10 === 0;
}

export default function CardPaymentModal({
  open,
  onClose,
  onConfirm,
  amountText = "",
}) {
  const [name, setName] = useState("");
  const [cardNumber, setCardNumber] = useState("");
  const [expiry, setExpiry] = useState("");
  const [cvc, setCvc] = useState("");
  const [err, setErr] = useState("");

  // ✅ flip state
  const [flipped, setFlipped] = useState(false);

  const masked = useMemo(() => {
    const d = onlyDigits(cardNumber);
    if (!d) return "•••• •••• •••• ••••";
    const last4 = d.slice(-4).padStart(4, "•");
    return `•••• •••• •••• ${last4}`;
  }, [cardNumber]);

  useEffect(() => {
    if (!open) {
      setErr("");
      setName("");
      setCardNumber("");
      setExpiry("");
      setCvc("");
      setFlipped(false);
    }
  }, [open]);

  useEffect(() => {
    function onEsc(e) {
      if (e.key === "Escape") onClose?.();
    }
    if (open) window.addEventListener("keydown", onEsc);
    return () => window.removeEventListener("keydown", onEsc);
  }, [open, onClose]);

  function validate() {
    if (!name.trim()) return "Въведи име на картодържател.";
    const digits = onlyDigits(cardNumber);
    if (digits.length !== 16) return "Картата трябва да е 16 цифри.";
    if (!luhnCheck(digits)) return "Невалиден номер (провери цифрите).";
    const ex = expiry.trim();
    if (!/^\d{2}\/\d{2}$/.test(ex)) return "Валидност трябва да е MM/YY.";
    const mm = Number(ex.slice(0, 2));
    if (mm < 1 || mm > 12) return "Невалиден месец.";
    if (onlyDigits(cvc).length !== 3) return "CVC трябва да е 3 цифри.";
    return "";
  }

  function submit() {
    const v = validate();
    if (v) return setErr(v);
    setErr("");
    onConfirm?.({
      name: name.trim(),
      last4: onlyDigits(cardNumber).slice(-4),
    });
  }

  return (
    <AnimatePresence>
      {open && (
        <motion.div
          className="cardModal__backdrop"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onMouseDown={onClose}
        >
          <motion.div
            className="cardModal__panel"
            initial={{ opacity: 0, y: 20, scale: 0.98 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 10, scale: 0.98 }}
            transition={{ type: "spring", stiffness: 260, damping: 22 }}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="cardModal__header">
              <div>
                <div className="cardModal__title">Плащане с карта (симулация)</div>
                <div className="cardModal__sub">
                  {amountText ? `Сума: ${amountText}` : "Въведи данните за картата"}
                </div>
              </div>

              <button className="cardModal__x" onClick={onClose} aria-label="Close">
                ✕
              </button>
            </div>

            {/* ✅ CodePen-style flip card */}
            <div className="ccWrap">
              <div className={`ccCard ${flipped ? "is-flipped" : ""}`}>
                <div className="ccCard__inner">
                  <div className="ccCard__front">
                    <div className="ccCard__bg" />
                    <div className="ccCard__glow" />

                    <div className="ccCard__contactless" aria-hidden="true">
                      <svg xmlns="http://www.w3.org/2000/svg" width="46" height="56">
                        <path
                          fill="none"
                          stroke="#f9f9f9"
                          strokeWidth="6"
                          strokeLinecap="round"
                          d="m35,3a50,50 0 0,1 0,50M24,8.5a39,39 0 0,1 0,39M13.5,13.55a28.2,28.5 0 0,1 0,28.5M3,19a18,17 0 0,1 0,18"
                        />
                      </svg>
                    </div>

                    <div className="ccCard__chip" />

                    <div className="ccCard__number">{masked}</div>

                    <div className="ccCard__holderLabel">CARDHOLDER</div>
                    <div className="ccCard__holder">{name.trim() || "YOUR NAME"}</div>

                    <div className="ccCard__expLabel">EXP</div>
                    <div className="ccCard__exp">{expiry || "MM/YY"}</div>
                  </div>

                  <div className="ccCard__back">
                    <div className="ccCard__stripe" />
                    <div className="ccCard__signature">
                      <span className="ccCard__signatureName">{name.trim() || "YOUR NAME"}</span>
                      <span className="ccCard__cvc">{(cvc || "•••").slice(0, 3)}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div className="ccHint">Фокус в CVC → flip</div>
            </div>

            {err && <div className="cardModal__error">{err}</div>}

            <div className="cardModal__form">
              <label className="cardModal__field">
                Име на картодържател
                <input
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Ivan Ivanov"
                  autoFocus
                />
              </label>

              <label className="cardModal__field">
                Номер на карта
                <input
                  value={cardNumber}
                  onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
                  placeholder="4242 4242 4242 4242"
                  inputMode="numeric"
                />
              </label>

              <div className="cardModal__grid2">
                <label className="cardModal__field">
                  Валидност
                  <input
                    value={expiry}
                    onChange={(e) => setExpiry(formatExpiry(e.target.value))}
                    placeholder="MM/YY"
                    inputMode="numeric"
                    onFocus={() => setFlipped(false)}
                  />
                </label>

                <label className="cardModal__field">
                  CVC
                  <input
                    value={cvc}
                    onChange={(e) => setCvc(onlyDigits(e.target.value).slice(0, 3))}
                    placeholder="123"
                    inputMode="numeric"
                    onFocus={() => setFlipped(true)}   // ✅ flip on focus
                    onBlur={() => setFlipped(false)}   // ✅ unflip
                  />
                </label>
              </div>

              <div className="cardModal__actions">
                <button className="cardModal__btn" type="button" onClick={onClose}>
                  Отказ
                </button>
                <button className="cardModal__btn cardModal__btn--primary" type="button" onClick={submit}>
                  Плати
                </button>
              </div>

              <div className="cardModal__tiny">
                Тест: 4242 4242 4242 4242 • Това е симулация.
              </div>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
