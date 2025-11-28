import React from 'react';
// Import any necessary styling components (e.g., from a UI library or your CSS)

const AboutPage = () => {
  return (
    <div className="about-container">
      <h1>About ECORoute</h1>
      
      {/* 1. Intro Section */}
      <section className="about-section" id="intro">
        <h2>Making Every Journey Smarter</h2>
        <p>We are a team of passionate developers based in Malaysia who recognized a critical flaw in everyday navigation. For too long, commuters have been forced to choose routes based only on speed, leading to hidden costs and invisible environmental damage. <strong>ECORoute</strong> was born to change that.</p>
      </section>

      {/* 2. Mission Section */}
      <section className="about-section" id="mission">
        <h3>Our Mission: Transparent Travel</h3>
        <p>Our core mission is to bridge the gap between user priorities (saving money and saving time) and environmental responsibility. We aim to provide the most transparent routing options available, ensuring you have the data to make the best decision for your wallet and the planet.</p>
      </section>

      {/* 3. The Difference Section - Highlighted Value */}
      <section className="about-section" id="the-difference">
        <h3>The ECORoute Difference</h3>
        <p>We don't just show you the fastest route. Our unique, personalized methodology integrates four critical data points for every option:</p>
        <ul>
          <li><strong>Time</strong></li>
          <li><strong>Distance</strong></li>
          <li><strong>Estimated Fuel Consumption</strong></li>
          <li><strong>Estimated CO<sub>2</sub> Emission</strong></li>
        </ul>
        <p>By factoring in your specific car model and real-time traffic patterns, we provide the intelligence you need to choose the most efficient path.</p>
      </section>

      {/* 4. Impact Section */}
      <section className="about-section" id="impact">
        <h3>Driving Sustainable Change</h3>
        <p>When you use ECORoute, you are contributing to a healthier environment. By making the <strong>CO<sub>2</sub> impact visible</strong>, we empower you to reduce your carbon footprint and actively contribute to lowering local pollution and haze. Small changes on your daily commute lead to big impacts for our communities.</p>
      </section>

      {/* 5. Team Section */}
      <section className="about-section" id="team">
        <h3>Meet the Team</h3>
        <p>We are [Your Team Name, e.g., Team Compass], a group dedicated to building smart, sustainable solutions for Southeast Asian commuters. Thank you for driving with us!</p>
        {/* Optional: Add contact info or links */}
      </section>
    </div>
  );
};

export default AboutPage;