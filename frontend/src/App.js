// Example for a React component, e.g., in frontend/src/App.js
import React, { useState, useEffect } from 'react';

function App() {
  // State to store the message from the backend
  const [message, setMessage] = useState('');
  // State to store any errors
  const [error, setError] = useState(null);

  // useEffect hook to fetch data when the component mounts
  useEffect(() => {
    // We define an async function inside the effect
    const fetchData = async () => {
      try {
        // Fetch data from your backend's /hello endpoint
        const response = await fetch('http://localhost:8080/hello');

        // Check if the request was successful
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Since the backend returns plain text, we use .text()
        const data = await response.text();
        
        // Update the state with the message from the backend
        setMessage(data);
      } catch (e) {
        // If an error occurs, update the error state
        setError(e.message);
        console.error("Failed to fetch data:", e);
      }
    };

    // Call the function to fetch data
    fetchData();
  }, []); // The empty array [] means this effect runs only once when the component mounts

  // Render the component's UI
  return (
    <div>
      <h1>Frontend Application</h1>
      {/* Display a loading message until the data is fetched */}
      {!message && !error && <p>Loading message from backend...</p>}
      
      {/* Display the message from the backend if it exists */}
      {message && <p><strong>Message from backend:</strong> {message}</p>}

      {/* Display an error message if the fetch failed */}
      {error && <p><strong>Error:</strong> {error}</p>}
    </div>
  );
}

export default App;
