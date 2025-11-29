import React from 'react';
import './CssPages/CO2Calculator.css';

function CO2Calculator({ distance, duration, fuelUsed }) {
  // Parse distance (remove "km" and convert to number)
  const distanceKm = distance ? parseFloat(distance.replace(/[^0-9.]/g, '')) : 0;
  
  // Parse duration (handle hours and minutes)
  const parseDuration = (durationStr) => {
    if (!durationStr) return 0;
    const hours = durationStr.match(/(\d+)\s*hour/);
    const mins = durationStr.match(/(\d+)\s*min/);
    return (hours ? parseInt(hours[1]) * 60 : 0) + (mins ? parseInt(mins[1]) : 0);
  };
  const durationMins = parseDuration(duration);
  
  // Parse fuel consumption (remove "L" and convert to number)
  const fuelLiters = fuelUsed ? parseFloat(fuelUsed.replace(/[^0-9.]/g, '')) : 0;
  
  // Calculate CO2 emissions (average: 2.31 kg CO2 per liter of gasoline)
  const co2Emissions = (fuelLiters * 2.31).toFixed(2);
  
  // Calculate fuel cost (using recent RON95 price in Malaysia)
  const fuelCostPerLiter = 2.05; 
  const totalFuelCost = (fuelLiters * fuelCostPerLiter).toFixed(2);
  
  // Average speed
  const avgSpeed = durationMins > 0 ? (distanceKm / (durationMins / 60)).toFixed(1) : 0;

  return (
    <div className="co2-calculator">
      <div className="calculator-content">
        <div className="stat-row">
          <span className="stat-text">{distanceKm.toFixed(1)} km</span>
          <span className="stat-text">{duration || 'N/A'}</span>
          <span className="stat-text">{fuelLiters.toFixed(2)} L</span>
        </div>
        <div className="stat-row">
          <span className="stat-text highlight">{co2Emissions} kg CO₂</span>
          <span className="stat-text">RM{totalFuelCost}</span>
          <span className="stat-text">{avgSpeed} km/h</span>
        </div>
      </div>
    </div>
  );
}

export default CO2Calculator;
