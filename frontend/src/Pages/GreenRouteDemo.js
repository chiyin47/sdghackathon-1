import React, { useState } from 'react';

function GreenRouteDemo() {
  const [origin, setOrigin] = useState('');
  const [destination, setDestination] = useState('');
  const [result, setResult] = useState('');

  const handleClick = () => {
    const url = `http://localhost:8080/route?origin=${origin}&destination=${destination}`;
    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        setResult(data.content);
      })
      .catch((error) => {
        console.error('Error:', error);
        setResult('Error fetching data.');
      });
  };

  return (
    <div>
      <h2>Green Route Demo</h2>
      <div>
        <label htmlFor="origin">Origin:</label>
        <input
          type="text"
          id="origin"
          value={origin}
          onChange={(e) => setOrigin(e.target.value)}
        />
      </div>
      <div>
        <label htmlFor="destination">Destination:</label>
        <input
          type="text"
          id="destination"
          value={destination}
          onChange={(e) => setDestination(e.target.value)}
        />
      </div>
      <button onClick={handleClick}>Get Green Route</button>
      <div id="result">{result}</div>
    </div>
  );
}

export default GreenRouteDemo;
