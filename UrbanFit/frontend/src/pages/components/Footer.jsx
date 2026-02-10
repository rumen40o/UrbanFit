import React from 'react';
import './css/Navbar.css';

export default function Footer(){
  return (
    <footer style={{padding:'28px 0', marginTop:40}}>
      <div style={{maxWidth:1200, margin:'0 auto', display:'flex', justifyContent:'space-between', alignItems:'center', gap:12}}>
        <div style={{display:'flex',flexDirection:'column'}}>
          <div style={{fontWeight:800, fontSize:18}}>UrbanFit</div>
          <div className="muted" style={{fontSize:13}}>© {new Date().getFullYear()} UrbanFit — all rights reserved</div>
        </div>

        <div style={{display:'flex', gap:12}}>
          <a href="#" className="muted">Privacy</a>
          <a href="#" className="muted">Terms</a>
          <a href="#" className="muted">Contact</a>
        </div>
      </div>
    </footer>
  )
}
