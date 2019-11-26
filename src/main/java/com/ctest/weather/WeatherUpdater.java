package com.ctest.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class WeatherUpdater {

    public WeatherUpdater(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public boolean Update(String city, String weatherProvider) {
        if (weatherProvider.equals("Yandex")) return RequestYandex(city);
        if (weatherProvider.equals("OpenWeather")) return RequestOpenWeather(city);
        return false;
    }

    public static boolean inUnknownCity(String city) {
        return unknownCity.contains(city);
    }

    private static Map<String, String> coordinatesCache = new ConcurrentHashMap<String, String>()
    {{
        put("Екатеринбург", "lat=56.836331&lon=60.605546");
        put("Москва", "lat=55.748952&lon=37.620076");
        put("Нью-Йорк", "lat=40.726063&lon=-73.822881");
        put("Амстердам", "lat=52.377109&lon=4.897162");
        put("Магадан", "lat=59.563922&lon=150.814959");
    }};

    private static Set<String> unknownCity = new HashSet<>();

    private final WeatherRepository weatherRepository;

    private String Coordinates(String city) {
        if (!coordinatesCache.containsKey(city)) {
            String coordinates = Geocode(city);
            if (coordinates != null) {
                coordinatesCache.put(city, coordinates);
                return coordinates;
            }
        } else {
            return coordinatesCache.get(city);
        }
        return null;
    }

    private String Geocode(String city) {
        unknownCity.add(city);
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyCjzksE_SwyV6AQSJw9EoL3Lq9T0jY3ekg",
                                    city);
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange(url,
                HttpMethod.GET, requestEntity, String.class);
        String response = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        assert response != null;

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            String status = rootNode.path("status").asText();
            if (status.equals("ZERO_RESULTS")) return null;
            String lat = rootNode.path("results").get(0).path("geometry").path("location").path("lat").asText();
            String lng = rootNode.path("results").get(0).path("geometry").path("location").path("lng").asText();
            String address = rootNode.path("results").get(0).path("formatted_address").asText();
            System.out.println(address);
            unknownCity.remove(city);
            if (status.equals("OK")) return "lat=" + lat + "&lon=" + lng;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean RequestYandex(String city) {
        String coordinates = Coordinates(city);
        if (coordinates == null) return false;
        String url = String.format("https://api.weather.yandex.ru/v1/forecast?%s&limit=1&extra=true",
                                    coordinates);
        String body = "";

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Yandex-API-Key", "d43de752-4d15-4b22-9093-72e8dc7794b0");
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String response = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            assert response != null;
            JsonNode rootNode = objectMapper.readTree(response);
            String temp = rootNode.path("fact").path("temp").asText();
            String wind = rootNode.path("fact").path("wind_speed").asText();
            String pressure = rootNode.path("fact").path("pressure_pa").asText();
            String humidity = rootNode.path("fact").path("humidity").asText();
            String cloudness = rootNode.path("fact").path("cloudness").asText();

            Weather weather = new WeatherBuilder()
                    .withCloudness(cloudness)
                    .withHumidity(humidity)
                    .withPressure(pressure)
                    .withTemp(temp)
                    .withWind(wind)
                    .withCity(city)
                    .withWeatherProvider("Yandex").build();

            weatherRepository.save(weather);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean RequestOpenWeather(String city) {
        String coordinates = Coordinates(city);
        if (coordinates == null) return false;
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?%s&units=metric&APPID=c351d6bef8d8ca2005cd43ddbad73a04",
                                        coordinates);
        String body = "";

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
     //   HttpStatus status = responseEntity.getStatusCode();
        String response = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            assert response != null;
            JsonNode rootNode = objectMapper.readTree(response);
            String temp = rootNode.path("main").path("temp").asText();
            String wind = rootNode.path("wind").path("speed").asText();
            String pressure = rootNode.path("main").path("pressure").asText();
            String humidity = rootNode.path("main").path("humidity").asText();
            String cloudness = rootNode.path("clouds").path("all").asText();

            Weather weather = new WeatherBuilder()
                    .withCloudness(cloudness)
                    .withHumidity(humidity)
                    .withPressure(pressure)
                    .withTemp(temp)
                    .withWind(wind)
                    .withCity(city)
                    .withWeatherProvider("OpenWeather").build();

            weatherRepository.save(weather);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;

    }

}
