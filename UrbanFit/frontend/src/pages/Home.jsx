import { Link } from "react-router-dom";
import "./Css/home.css";

export default function Home() {
  return (
    <div className="home">
      {/* HERO */}
      <section className="home__hero">
        <h1>UrbanFit</h1>
        <p>
          Modern urban clothing shop with secure login, cart,
          checkout, reviews and admin panel.
        </p>

        <div className="home__hero-actions">
          <Link to="/products">
            <button className="btn btn--primary">Browse Products</button>
          </Link>
        </div>
      </section>

      {/* CTA */}
      <section className="home__cta">
        <h2>Ready to explore our products?</h2>
        <Link to="/products">
          <button className="btn btn--primary">View Products</button>
        </Link>
      </section>
    </div>
  );
}
