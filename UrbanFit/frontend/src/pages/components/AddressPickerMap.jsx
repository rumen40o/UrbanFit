import { useMemo, useState } from "react";
import { MapContainer, TileLayer, Marker, useMapEvents } from "react-leaflet";
import L from "leaflet";

// Fix за marker иконката (Vite/React често чупи default icon paths)
import marker2x from "leaflet/dist/images/marker-icon-2x.png";
import marker1x from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: marker2x,
  iconUrl: marker1x,
  shadowUrl: markerShadow,
});

async function reverseGeocode(lat, lon) {
  // Nominatim: ползвай user-agent/headers (axios го прави), тук fetch е ок.
  const url =
    `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lon}&zoom=18&addressdetails=1`;
  const res = await fetch(url, {
    headers: {
      "Accept": "application/json",
      // Някои инстанции не харесват празен UA; браузърът си има UA, така че е ок.
    },
  });
  if (!res.ok) throw new Error("Reverse geocoding failed");
  return res.json();
}

function ClickHandler({ onPick }) {
  useMapEvents({
    click: (e) => {
      onPick(e.latlng);
    },
  });
  return null;
}

export default function AddressPickerMap({
  initialCenter = { lat: 42.6977, lng: 23.3219 }, // София default
  valueLatLng,
  onChangeLatLng,
  onResolvedAddress,
}) {
  const [status, setStatus] = useState("");
  const [busy, setBusy] = useState(false);

  const pos = useMemo(() => {
    return valueLatLng ?? initialCenter;
  }, [valueLatLng, initialCenter]);

  async function pick(latlng) {
    onChangeLatLng?.(latlng);
    setBusy(true);
    setStatus("Търся адрес…");

    try {
      const data = await reverseGeocode(latlng.lat, latlng.lng);

      const pretty =
        data?.display_name ||
        `${latlng.lat.toFixed(6)}, ${latlng.lng.toFixed(6)}`;

      onResolvedAddress?.(pretty);
      setStatus("Адресът е попълнен ✅");
    } catch (e) {
      setStatus("Не успях да намеря адрес. Попълни ръчно.");
    } finally {
      setBusy(false);
      // махаме статус след малко
      setTimeout(() => setStatus(""), 2500);
    }
  }

  return (
    <div className="addrMap">
      <div className="addrMap__hint">
        Кликни на картата, за да избереш точка за доставка.
      </div>

      <div className="addrMap__box">
        <MapContainer
          center={pos}
          zoom={14}
          style={{ height: 320, width: "100%" }}
          scrollWheelZoom
        >
          <TileLayer
            attribution='&copy; OpenStreetMap contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          <ClickHandler onPick={pick} />

          {pos && <Marker position={pos} />}
        </MapContainer>
      </div>

      {(busy || status) && (
        <div className="addrMap__status">
          {busy ? "Работя…" : status}
        </div>
      )}
    </div>
  );
}
