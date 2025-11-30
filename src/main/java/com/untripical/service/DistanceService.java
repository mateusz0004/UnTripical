package com.untripical.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class DistanceService {
    @Value("${google.maps.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public double getDistanceInKm(String origin, String destination) throws Exception {

        String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&mode=driving&key=%s",
                URLEncoder.encode(origin, StandardCharsets.UTF_8),
                URLEncoder.encode(destination, StandardCharsets.UTF_8),
                apiKey
        );

        String response = restTemplate.getForObject(url, String.class);
        JsonNode root = objectMapper.readTree(response);

        JsonNode element = root.path("rows").get(0).path("elements").get(0);
        String status = element.path("status").asText();

        if (!"OK".equals(status)) {
            throw new Exception("Google Maps API error: " + status);
        }

        double distanceMeters = element.path("distance").path("value").asDouble();
        return distanceMeters / 1000.0;
    }
}