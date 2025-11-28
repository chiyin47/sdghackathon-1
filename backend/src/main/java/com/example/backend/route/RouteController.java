package com.example.backend.route;

import com.google.maps.model.DirectionsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteController {

    private final DirectionsService directionsService;
    private final GreenRouteService greenRouteService;
    private long counter = 0;

    @Autowired
    public RouteController(DirectionsService directionsService, GreenRouteService greenRouteService) {
        this.directionsService = directionsService;
        this.greenRouteService = greenRouteService;
    }

    @GetMapping("/route")
    public Route route(@RequestParam(value = "origin") String origin,
                       @RequestParam(value = "destination") String destination) {
        counter++;
        try {
            DirectionsResult directionsResult = directionsService.getDirections(origin, destination);
            DirectionsResult greenRoute = greenRouteService.getGreenRoute(directionsResult);
            if (greenRoute.routes != null && greenRoute.routes.length > 0) {
                return new Route(counter, greenRoute.routes[0].summary);
            } else {
                return new Route(counter, "No routes found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Route(counter, "Error while fetching directions.");
        }
    }
}