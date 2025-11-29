import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import HomePage from "./Pages/HomePage";
import HowItWorks from "./Pages/HowItWorks";
import GreenRouteDemo from "./Pages/GreenRouteDemo";
import AboutPage from "./Pages/AboutPage";
import './App.css';

function App() {
  return (
    <Router>
      <nav className="navbar">
        
        {/* LEFT SIDE LOGO */}
        <div className="logo">
          <Link to="/">
            <img
              src={process.env.PUBLIC_URL + "/EcoRouteLogo.png"}
              alt="EcoRoute Logo"
              className="logo-image"
            />
          </Link>
        </div>

        {/* CENTERED MENU */}
        <div className="menu">
          <Link to="/">Home</Link>
          <Link to="/how-it-works">How it works</Link>
          <Link to="/demo">EcoRouter</Link>
          <Link to="/about">About us</Link>
        </div>
      </nav>

      {/* PAGE ROUTES */}
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/how-it-works" element={<HowItWorks />} />
        <Route path="/demo" element={<GreenRouteDemo />} />
        <Route path="/about" element={<AboutPage />} />
      </Routes>
    </Router>
  );
}

export default App;
