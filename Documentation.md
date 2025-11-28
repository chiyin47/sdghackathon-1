# Green Route Application

This application helps users find the most fuel-efficient route between two points. It consists of a React frontend and a Spring Boot backend.

## Running the Frontend

1.  Navigate to the `frontend` directory.
2.  Install the dependencies: `npm install`
3.  Start the development server: `npm start`
4.  The application will be available at `http://localhost:3000`.

## Running the Backend

1.  Open the `backend` directory in your favorite Java IDE.
2.  Obtain a Google Maps API key.
3.  Open the `src/main/resources/application.properties` file and replace `YOUR_API_KEY` with your actual API key.
4.  Run the `BackendApplication` class.
5.  The backend will be available at `http://localhost:8080`.

## How to Use

1.  Open the application in your browser.
2.  Navigate to the "Green Route Demo" page.
3.  Enter an origin and a destination.
4.  Click the "Get Green Route" button.
5.  The most fuel-efficient route will be displayed on the page.
