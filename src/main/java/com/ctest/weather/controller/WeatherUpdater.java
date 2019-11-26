package com.ctest.weather.controller;

import com.ctest.weather.TokensConfig;
import com.ctest.weather.model.Weather;
import com.ctest.weather.model.WeatherBuilder;
import com.ctest.weather.model.WeatherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aeonbits.owner.ConfigFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class WeatherUpdater {

    private final String TokenGoogleApi;
    private final String TokenYandexApi;
    private final String TokenOpenWeatherApi;

    public WeatherUpdater(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
        TokensConfig cfg = ConfigFactory.create(TokensConfig.class);
        TokenGoogleApi = cfg.TokenGoogleApi();
        TokenYandexApi = cfg.TokenYandexApi();
        TokenOpenWeatherApi = cfg.TokenOpenWeatherApi();
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

    private String Request(String url, boolean header) {
        String body = "";
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        if (header)
            headers.add("X-Yandex-API-Key", TokenYandexApi);

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
        return responseEntity.getBody();
    }

    private String Geocode(String city) {
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
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?%s&units=metric&APPID=" + TokenOpenWeatherApi,
                                        coordinates);

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
                    .withWeatherProvider("OpenWeather").build();

            weatherRepository.save(weather);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;

    }

}
