package com.example.backend.route;

import com.google.maps.model.DirectionsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GreenRouteService {

    private final AIGreenRouteService aiGreenRouteService;

    @Autowired
    public GreenRouteService(AIGreenRouteService aiGreenRouteService) {
        this.aiGreenRouteService = aiGreenRouteService;
    }

    public DirectionsResult getGreenRoute(DirectionsResult directionsResult) {
        return aiGreenRouteService.findBestRoute(directionsResult);
    }
}