import "./css/ConfirmDialog.css";

export default function ConfirmDialog({
  open,
  title = "Confirm",
  message = "",
  confirmText = "Yes",
  cancelText = "Cancel",
  danger = false,
  onConfirm,
  onClose,
}) {
  if (!open) return null;

  return (
    <div className="cd__backdrop" onMouseDown={onClose}>
      <div className="cd__panel" onMouseDown={(e) => e.stopPropagation()}>
        <div className="cd__title">{title}</div>
        <div className="cd__msg">{message}</div>

        <div className="cd__actions">
          <button className="cd__btn" onClick={onClose}>{cancelText}</button>
          <button
            className={`cd__btn ${danger ? "cd__btn--danger" : "cd__btn--primary"}`}
            onClick={onConfirm}
          >
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}