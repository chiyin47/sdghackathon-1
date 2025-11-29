package com.example.backend.route;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

@Service
public class AIModelService {

  @Value("${gemini.api.key}")
  private String apiKey;

  @Value("${gemini.model}")
  private String model;

  private final RestTemplate restTemplate = new RestTemplate();

  @PostConstruct
  public void init() {
    System.out.println("--- DEBUG: AIModelService ---");
    if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${GEMINI_API_KEY}")) {
      System.out.println("GEMINI_API_KEY is NOT loaded or is empty.");
      System.out.println("Please ensure the GEMINI_API_KEY environment variable is set and the application is restarted.");
    } else {
      System.out.println("GEMINI_API_KEY is loaded successfully.");
      // For security, only show the first few and last few characters
      if (apiKey.length() > 8) {
          System.out.println("API Key hint: " + apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4));
      }
    }
    System.out.println("--- END DEBUG: AIModelService ---");
  }

  /**
   * Sends user message to Gemini AI and returns the AI response.
   * 
   * @param userMessage The message from the user
   * @return AI response text
   */
  public String getAIResponse(String userMessage) {
    if (userMessage == null || userMessage.isBlank()) {
      return "Please provide a valid message.";
    }

    try {
      // Gemini AI REST endpoint
      String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

      // Build JSON payload
      String payload = """
          {
            "contents": [{"parts": [{"text": "%s"}]}],
            "generationConfig": {
                "temperature": 0.7,
                "maxOutputTokens": 2048
            }
          }
          """.formatted(userMessage);

      // Set headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      // Removed setBearerAuth, as API key is now in the URL

      HttpEntity<String> entity = new HttpEntity<>(payload, headers);

      // Make POST request
      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          entity,
          new ParameterizedTypeReference<Map<String, Object>>() {
          });

      Map<String, Object> body = response.getBody();
      System.out.println("Gemini API Response Body: " + body);
      if (body != null && body.containsKey("candidates")) {
        Object candidatesObj = body.get("candidates");
        if (candidatesObj instanceof List<?> candidatesList && !candidatesList.isEmpty()) {
          Object firstCandidate = candidatesList.get(0);
          if (firstCandidate instanceof Map<?, ?> firstMap) {
            Object content = firstMap.get("content");
            if (content instanceof Map<?, ?> contentMap) {
              Object parts = contentMap.get("parts");
              if (parts instanceof List<?> partsList && !partsList.isEmpty()) {
                Object part = partsList.get(0);
                if (part instanceof Map<?, ?> partMap) {
                  Object text = partMap.get("text");
                  if (text != null) {
                    return text.toString();
                  }
                }
              }
            }
          }
        }
      }

      return "AI did not return a response.";

    } catch (Exception e) {
      // Log the error and return a friendly message
      e.printStackTrace();
      return "Error contacting AI: " + e.getMessage();
    }
  }
}
