package com.ctest.weather.controller;

import com.ctest.weather.model.Weather;
import com.ctest.weather.model.WeatherBuilder;
import com.ctest.weather.model.WeatherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeatherUpdater {

    @Value("${token.google}")
    private String TokenGoogleApi;
    @Value("${token.yandex}")
    private String TokenYandexApi;
    @Value("${token.openweather}")
    private String TokenOpenWeatherApi;

    private final RestTemplate rest;
    private final WeatherRepository weatherRepository;

    public WeatherUpdater(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
        this.rest = new RestTemplateBuilder().build();
    }

    public boolean Update(String city, @NotNull String weatherProvider) {
        if (weatherProvider.equals("Yandex")) return RequestYandex(city);
        if (weatherProvider.equals("OpenWeather")) return RequestOpenWeather(city);
        return false;
    }

    public static boolean inUnknownCity(String city) {
        return unknownCity.contains(city);
    }

    private static Set<String> unknownCity = new HashSet<>();

    private static Map<String, Pair<String, String>> coordinatesCache = new ConcurrentHashMap<String, Pair<String, String>>()
    {{
        put("Екатеринбург", Pair.of("lat=56.836331&lon=60.605546", "Россия, Свердловская область"));
        put("Москва", Pair.of("lat=55.748952&lon=37.620076", "Россия"));
        put("Нью-Йорк", Pair.of("lat=40.726063&lon=-73.822881", "США"));
        put("Амстердам", Pair.of("lat=52.377109&lon=4.897162", "Нидерланды"));
        put("Магадан", Pair.of("lat=59.563922&lon=150.814959", "Россия, Магаданская область"));
    }};

    @Nullable
    private Pair<String, String> Coordinates(String city) {
        if (!coordinatesCache.containsKey(city)) {
            Pair<String, String> coordinates = Geocode(city);
            if (coordinates != null) {
                coordinatesCache.put(city, coordinates);
                return coordinates;
            }
        } else {
            return coordinatesCache.get(city);
        }
        return null;
    }

    String Request(String url, boolean header) {
        String body = "";
        HttpHeaders headers = new HttpHeaders();

        if (header)
            headers.add("X-Yandex-API-Key", TokenYandexApi);

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
        return responseEntity.getBody();
    }

    private Pair<String, String> Geocode(String city) {
        unknownCity.add(city);
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=" + TokenGoogleApi,
                                    city);

        String response = Request(url, false);

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
            if (status.equals("OK")) return Pair.of("lat=" + lat + "&lon=" + lng, address);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean RequestYandex(String city) {
        Pair<String, String> coordinates = Coordinates(city);
        if (coordinates == null) return false;
        String url = String.format("https://api.weather.yandex.ru/v1/forecast?%s&limit=1&extra=true",
                                    coordinates.getFirst());

        String response = Request(url, true);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            assert response != null;
            JsonNode rootNode = objectMapper.readTree(response);
            String temp = rootNode.path("fact").path("temp").asText();
            String wind = rootNode.path("fact").path("wind_speed").asText();
            String pressure = rootNode.path("fact").path("pressure_pa").asText();
            String humidity = rootNode.path("fact").path("humidity").asText();
            String cloudness = rootNode.path("fact").path("cloudness").asText();
            float cloud = Float.parseFloat(cloudness) * 100;

            Weather weather = new WeatherBuilder()
                    .withCloudness(String.format("%.0f", cloud))
                    .withHumidity(humidity)
                    .withPressure(pressure)
                    .withTemp(temp)
                    .withWind(wind)
                    .withCity(city)
                    .withAdress(coordinates.getSecond())
                    .withWeatherProvider("Yandex").build();

            weatherRepository.save(weather);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean RequestOpenWeather(String city) {
        Pair<String, String> coordinates = Coordinates(city);
        if (coordinates == null) return false;
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?%s&units=metric&APPID=" + TokenOpenWeatherApi,
                                        coordinates.getFirst());

        String response = Request(url, false);

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
                    .withAdress(coordinates.getSecond())
                    .withWeatherProvider("OpenWeather").build();

            weatherRepository.save(weather);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;

    }

}
