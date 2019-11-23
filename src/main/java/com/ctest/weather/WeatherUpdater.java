package com.ctest.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeatherUpdater {

    private final Map<Integer, String> Coordinates = new HashMap<>()
    {{
        put(1, "lat=56.836331&lon=60.605546"); // Ekaterinburg
        put(2, "lat=55.748952&lon=37.620076"); // Moscow
        put(3, "lat=40.726063&lon=-73.822881"); // New-York
        put(4, "lat=52.377109&lon=4.897162"); // Amsterdam
        put(5, "lat=59.563922&lon=150.814959"); // Magadan
    }};

    private final WeatherRepository weatherRepository;

    public WeatherUpdater(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public void Update(int hash) {
        if (hash % 10 == 1) RequestYandex(hash);
        if (hash % 10 == 2) RequestOpenWeather(hash);
    }

    private void RequestYandex(int hash) {
        String url = String.format("https://api.weather.yandex.ru/v1/forecast?%s&limit=1&extra=true",
                                    Coordinates.get(hash / 10));
//        System.out.println(url);
//        System.out.println(hash);
//        System.out.println(Coordinates.get(hash));

        String body = "";

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Yandex-API-Key", "d43de752-4d15-4b22-9093-72e8dc7794b0");
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String response = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
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
                    .withId(hash).build();

            weatherRepository.save(weather);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void RequestOpenWeather(int hash) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?%s&units=metric&APPID=c351d6bef8d8ca2005cd43ddbad73a04",
                                    Coordinates.get(hash / 10));

        System.out.println(url);
        System.out.println(hash);
        System.out.println(Coordinates.get(hash / 10));

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
     //   HttpStatus status = responseEntity.getStatusCode();
        String response = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
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
                    .withId(hash).build();

            weatherRepository.save(weather);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
