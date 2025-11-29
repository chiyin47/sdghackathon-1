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

            // List to hold intermediate waypoints
            List<RouteResponse.LatLng> intermediateWaypoints = new ArrayList<>();

            if (route.legs != null && route.legs.length > 0) {
                distance = route.legs[0].distance.humanReadable;
                duration = route.legs[0].duration.humanReadable;
                distanceMeters = route.legs[0].distance.inMeters;
                durationSeconds = route.legs[0].duration.inSeconds;

                // Extract intermediate waypoints (endLocation of each leg, except the last one)
                for (int i = 0; i < route.legs.length - 1; i++) {
                    com.google.maps.model.LatLng legEndLocation = route.legs[i].endLocation;
                    intermediateWaypoints.add(new RouteResponse.LatLng(legEndLocation.lat, legEndLocation.lng));
                }
            }

            // Create a prompt for the AI model with more detailed requests
            String prompt = String.format(
                    "Analyze the fuel efficiency of a route covering %d meters with an estimated duration of %d seconds. " +
                    "Provide an estimated fuel consumption in liters for an average car. " +
                    "Classify its efficiency. " +
                    "Provide a brief driving recommendation. " +
                    "Suggest a strategy for peak hours. " +
                    "Response format:\n" +
                    "Fuel: X.XX liters\n" +
                    "Efficiency: [Classification]\n" +
                    "Recommendation: [Brief recommendation]\n" +
                    "Peak Hours Strategy: [Brief strategy]",
                    distanceMeters, durationSeconds);

            String aiResponse = aiModelService.getAIResponse(prompt);
            System.out.println("Raw AI Response for route " + routeNumber + ":\n" + aiResponse);

            String fuelPrediction = "Fuel prediction unavailable";
            double parsedFuel = Double.MAX_VALUE;

            String efficiencyClassification = "N/A";
            String drivingRecommendation = "No specific recommendation.";
            String peakHoursAdvice = "No specific peak hour advice.";
            String fullPredictionSummary = "No AI prediction available.";

            // Attempt to parse AI response for fuel prediction
            boolean aiFuelParsedSuccessfully = false;
            if (aiResponse != null && !aiResponse.isEmpty()) {
                Pattern fuelPattern = Pattern.compile("Fuel: ([\\d.]+) liters");
                Matcher fuelMatcher = fuelPattern.matcher(aiResponse);
                if (fuelMatcher.find()) {
                    String fuelValueStr = fuelMatcher.group(1);
                    try {
                        parsedFuel = Double.parseDouble(fuelValueStr);
                        fuelPrediction = String.format("%.2f liters", parsedFuel);
                        aiFuelParsedSuccessfully = true;
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse AI fuel prediction: '" + fuelValueStr + "' for route " + routeNumber + ". Resorting to rough estimation. " + e.getMessage());
                    }
                } else {
                    System.err.println("AI response did not contain expected 'Fuel: X liters' pattern for route " + routeNumber + ". Resorting to rough estimation. Response snippet: " + aiResponse.substring(0, Math.min(aiResponse.length(), 200)) + "...");
                }

                // If AI fuel prediction failed or was not found, use a rough estimation (this block is now correctly inside the main if)
                if (!aiFuelParsedSuccessfully) {
                    parsedFuel = (distanceMeters / 1000.0) * 0.08 + (durationSeconds * 0.0005);
                    fuelPrediction = String.format("Estimated: %.2f liters", parsedFuel);
                    System.out.println("Using rough fuel estimation for route " + routeNumber + ": " + fuelPrediction);
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

                // Fallback for AI-provided efficiency, recommendation, and peak hour advice if not parsed
                if (efficiencyClassification.equals("N/A")) {
                    efficiencyClassification = "Not explicitly classified by AI, based on fuel: " + (parsedFuel < 5.0 ? "Efficient" : (parsedFuel < 10.0 ? "Average" : "Less Efficient"));
                }
                if (drivingRecommendation.equals("No specific recommendation.")) {
                    drivingRecommendation = "General advice: Drive smoothly, avoid rapid acceleration/braking, maintain consistent speed. Check tire pressure regularly.";
                }
                if (peakHoursAdvice.equals("No specific peak hour advice.")) {
                    peakHoursAdvice = "General peak hour strategy: Consider leaving earlier/later, use real-time traffic apps, and explore less congested alternative routes.";
                }

                // Construct full prediction summary for display
                fullPredictionSummary = String.format(
                    "Fuel: %s\nEfficiency: %s\nRecommendation: %s\nPeak Hours Strategy: %s",
                    fuelPrediction,
                    efficiencyClassification,
                    drivingRecommendation,
                    peakHoursAdvice
                );
            } // This is the correct closing brace for if (aiResponse != null && !aiResponse.isEmpty())


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
            routeResponse.setWaypoints(intermediateWaypoints); // Set the extracted intermediate waypoints
            routeResponses.add(routeResponse);
        }

        // Sort routes to put the most fuel-efficient and potentially fastest first
        routeResponses.sort((r1, r2) -> {
            // Primary sort by parsedFuelConsumption (ascending - lower is better)
            int fuelCompare = Double.compare(r1.getParsedFuelConsumption(), r2.getParsedFuelConsumption());
            if (fuelCompare != 0) {
                return fuelCompare;
            }
            // Secondary sort by durationSeconds (ascending - lower is better), if fuel consumption is similar
            // Assuming duration can be parsed to seconds for comparison if needed, or using duration string directly
            // For simplicity, we'll try to parse it from the human-readable string.
            long duration1 = parseDurationToSeconds(r1.getDuration());
            long duration2 = parseDurationToSeconds(r2.getDuration());
            return Long.compare(duration1, duration2);
        });

        // After sorting, mark the first route (the best one) as green
        for (int i = 0; i < routeResponses.size(); i++) {
            if (i == 0) {
                routeResponses.get(i).setColor("green");
            } else {
                routeResponses.get(i).setColor("red");
            }
        }
        
        return routeResponses;
    }

    // Helper method to parse human-readable duration to seconds (rough estimation)
    private long parseDurationToSeconds(String durationString) {
        long totalSeconds = 0;
        if (durationString == null || durationString.isEmpty()) {
            return totalSeconds;
        }

        Pattern daysPattern = Pattern.compile("(\\d+)\\s*day");
        Pattern hoursPattern = Pattern.compile("(\\d+)\\s*hour");
        Pattern minsPattern = Pattern.compile("(\\d+)\\s*min");
        Pattern secondsPattern = Pattern.compile("(\\d+)\\s*sec");

        Matcher daysMatcher = daysPattern.matcher(durationString);
        if (daysMatcher.find()) {
            totalSeconds += Long.parseLong(daysMatcher.group(1)) * 24 * 60 * 60;
        }

        Matcher hoursMatcher = hoursPattern.matcher(durationString);
        if (hoursMatcher.find()) {
            totalSeconds += Long.parseLong(hoursMatcher.group(1)) * 60 * 60;
        }

        Matcher minsMatcher = minsPattern.matcher(durationString);
        if (minsMatcher.find()) {
            totalSeconds += Long.parseLong(minsMatcher.group(1)) * 60;
        }
        
        Matcher secondsMatcher = secondsPattern.matcher(durationString);
        if (secondsMatcher.find()) {
            totalSeconds += Long.parseLong(secondsMatcher.group(1));
        }

        return totalSeconds;
    }
}