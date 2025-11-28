import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import HomePage from "./Pages/HomePage";
import HowItWorks from "./Pages/HowItWorks";
import GreenRouteDemo from "./Pages/GreenRouteDemo";
import CO2Calculator from "./Pages/CO2Calculator";
import AboutPage from "./Pages/AboutPage";
import './App.css';  // Import the CSS file

function App() {
  const [message, setMessage] = useState('');
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch('http://localhost:8080/hello');
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.text();
        setMessage(data);
      } catch (e) {
        setError(e.message);
        console.error("Failed to fetch data:", e);
      }
    };
    fetchData();
  }, []);

  return (
    <Router>
      <nav className="navbar">
        <div className="logo">EcoRoute</div>
        <div className="menu">
          <Link to="/">Home</Link>
          <Link to="/how-it-works">How It Works</Link>
          <Link to="/demo">Green Route Demo</Link>
          <Link to="/calculator">CO₂ Calculator</Link>
          <Link to="/about">About</Link>
        </div>
      </nav>

      <div className="backend-message">
        {!message && !error && <p>Loading message from backend...</p>}
        {message && <p><strong>Message from backend:</strong> {message}</p>}
        {error && <p><strong>Error:</strong> {error}</p>}
      </div>

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/how-it-works" element={<HowItWorks />} />
        <Route path="/demo" element={<GreenRouteDemo />} />
        <Route path="/calculator" element={<CO2Calculator />} />
        <Route path="/about" element={<AboutPage />} />
      </Routes>
    </Router>
  );
}

export default App;
