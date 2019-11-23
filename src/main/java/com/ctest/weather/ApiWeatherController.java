package com.ctest.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/weather")
public class ApiWeatherController {

    private final WeatherRepository weatherRepository;

    public ApiWeatherController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public String RequestYandex() {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Yandex-API-Key", "d43de752-4d15-4b22-9093-72e8dc7794b0");
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity =
                rest.exchange("https://api.weather.yandex.ru/v1/forecast?lat=60.61&lon=56.84&limit=1&extra=true",
                        HttpMethod.GET, requestEntity, String.class);

        String response = responseEntity.getBody();
        System.out.println(response);

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
                    .withId(11).build();

            weatherRepository.save(weather);
            return temp + " " + wind + " " + pressure + " " + humidity + " " + new Date().getTime();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Fail";
    }

    @GetMapping("/yandex")
    public String getVisits() {
        return RequestYandex();
    }
}