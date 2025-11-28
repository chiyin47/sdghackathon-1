import React from 'react';
import Chatbot from './Chatbot';
import './CssPages/HomePage.css';

function HomePage() {
  return (
    <div className="home-page">
      <h1>Welcome to Your Routing Application</h1>
      <p>Plan your trips efficiently. Your AI assistant is here to help!</p>
      <Chatbot />
    </div>
  );
}

export default HomePage;