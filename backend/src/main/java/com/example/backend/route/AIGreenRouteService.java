package com.example.backend.route;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.internal.PolylineEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIGreenRouteService {

    @Autowired
    private AIModelService aiModelService;

    public DirectionsResult findBestRoute(DirectionsResult directionsResult) {
        // For now, simply return the original directionsResult.
        // The logic to determine the "best" route based on AI prediction will be implemented here.
        processRoutes(directionsResult); // Process the routes to get AI predictions
        return directionsResult;
    }

    public List<RouteResponse> processRoutes(DirectionsResult result) {
        List<RouteResponse> routeResponses = new ArrayList<>();

        if (result == null || result.routes == null || result.routes.length == 0) {
            return routeResponses;
        }

        int routeNumber = 1;
        for (DirectionsRoute route : result.routes) {
            String distance = "N/A";
            String duration = "N/A";
            long distanceMeters = 0;
            long durationSeconds = 0;

            if (route.legs != null && route.legs.length > 0) {
                distance = route.legs[0].distance.humanReadable;
                duration = route.legs[0].duration.humanReadable;
                distanceMeters = route.legs[0].distance.inMeters;
                durationSeconds = route.legs[0].duration.inSeconds;
            }

            // Create a prompt for the AI model with more detailed requests
            String prompt = String.format(
                    "Analyze this route for fuel efficiency, providing a comprehensive AI-driven deep thought suggestion. " +
                    "The route has a distance of %d meters and an estimated duration of %d seconds. " +
                    "Please provide a precise estimate of fuel consumption in liters for an average car, *explicitly factoring in typical traffic patterns and potential congestion during peak hours for a route of this duration and distance*. " +
                    "Classify its overall efficiency (e.g., 'very efficient', 'efficient', 'average', 'less efficient'). " +
                    "Offer specific, actionable driving recommendations to maximize fuel savings and minimize environmental impact on this *exact route*. " +
                    "Also, provide detailed strategies to navigate this route during typical peak hours, suggesting specific times or alternative approaches to avoid congestion and further save fuel, considering its unique characteristics. " +
                    "Your response should be structured as follows:\n\n" +
                    "Fuel: X liters\n" +
                    "Efficiency: [Classification]\n" +
                    "Recommendation: [Detailed driving recommendations tailored to this route, including peak hour considerations]\n" +
                    "Peak Hours Strategy: [Specific strategies for avoiding congestion and saving fuel during peak times on this route]\n\n" +
                    "Provide clear and concise information for each section, focusing on practical advice.",
                    distanceMeters, durationSeconds);

            String aiResponse = aiModelService.getAIResponse(prompt);

            String fuelPrediction = "N/A";
            double parsedFuel = Double.MAX_VALUE; // Initialize with a high value for comparison
            String efficiencyClassification = "N/A";
            String drivingRecommendation = "No specific recommendation.";
            String peakHoursAdvice = "No specific peak hour advice.";
            String fullPredictionSummary = "No AI prediction available.";


            // Parse AI response
            if (aiResponse != null && !aiResponse.isEmpty()) {
                // Extract Fuel
                Pattern fuelPattern = Pattern.compile("Fuel: ([\\d.]+) liters");
                Matcher fuelMatcher = fuelPattern.matcher(aiResponse);
                if (fuelMatcher.find()) {
                    fuelPrediction = fuelMatcher.group(1) + " liters";
                    try {
                        parsedFuel = Double.parseDouble(fuelMatcher.group(1));
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse fuel prediction: " + fuelMatcher.group(1));
                    }
                }

                // Extract Efficiency
                Pattern efficiencyPattern = Pattern.compile("Efficiency: (.+)");
                Matcher efficiencyMatcher = efficiencyPattern.matcher(aiResponse);
                if (efficiencyMatcher.find()) {
                    efficiencyClassification = efficiencyMatcher.group(1).trim();
                }

                // Extract Recommendation
                Pattern recommendationPattern = Pattern.compile("Recommendation: (.+)", Pattern.DOTALL);
                Matcher recommendationMatcher = recommendationPattern.matcher(aiResponse);
                if (recommendationMatcher.find()) {
                    drivingRecommendation = recommendationMatcher.group(1).trim();
                    // Remove subsequent sections if they were accidentally captured
                    int peakHoursIndex = drivingRecommendation.indexOf("Peak Hours Strategy:");
                    if (peakHoursIndex != -1) {
                        drivingRecommendation = drivingRecommendation.substring(0, peakHoursIndex).trim();
                    }
                }

                // Extract Peak Hours Strategy
                Pattern peakHoursPattern = Pattern.compile("Peak Hours Strategy: (.+)", Pattern.DOTALL);
                Matcher peakHoursMatcher = peakHoursPattern.matcher(aiResponse);
                if (peakHoursMatcher.find()) {
                    peakHoursAdvice = peakHoursMatcher.group(1).trim();
                }

                // Construct full prediction summary for display
                fullPredictionSummary = String.format(
                    "Fuel: %s\nEfficiency: %s\nRecommendation: %s\nPeak Hours Strategy: %s",
                    fuelPrediction,
                    efficiencyClassification,
                    drivingRecommendation,
                    peakHoursAdvice
                );
            }

            RouteResponse routeResponse = new RouteResponse();
            routeResponse.setRouteNumber(routeNumber++);
            routeResponse.setDistance(distance);
            routeResponse.setDuration(duration);
            routeResponse.setFuelUsed(fuelPrediction); // Using AI prediction for fuel used
            routeResponse.setFuelSavingPrediction(fullPredictionSummary); // Store comprehensive AI response
            routeResponse.setParsedFuelConsumption(parsedFuel); // Store parsed fuel for comparison

            List<RouteResponse.LatLng> pathCoordinates = new ArrayList<>();
            if (route.overviewPolyline != null && route.overviewPolyline.getEncodedPath() != null) {
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                for (com.google.maps.model.LatLng latLng : decodedPath) {
                    pathCoordinates.add(new RouteResponse.LatLng(latLng.lat, latLng.lng));
                }
            }
            routeResponse.setCoordinates(pathCoordinates);
            routeResponses.add(routeResponse);
        }

        // Find the most fuel-efficient route and mark it green
        RouteResponse mostEfficientRoute = null;
        double minFuel = Double.MAX_VALUE;

        for (RouteResponse routeResponse : routeResponses) {
            if (routeResponse.getParsedFuelConsumption() < minFuel) {
                minFuel = routeResponse.getParsedFuelConsumption();
                mostEfficientRoute = routeResponse;
            }
            routeResponse.setColor("red"); // Default all to red first
        }

        if (mostEfficientRoute != null) {
            mostEfficientRoute.setColor("green"); // Mark the most efficient as green
        }

        return routeResponses;
    }
}