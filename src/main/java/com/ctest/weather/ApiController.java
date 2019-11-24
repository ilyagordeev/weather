package com.ctest.weather;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class ApiController {

    final WeatherRepository weatherRepository;

    public ApiController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @PostMapping("/weather")
    public Weather getWeather(HttpServletResponse response, HttpServletRequest request) {
        String city = request.getParameter("city");
        String weatherProvider = request.getParameter("weatherProvider");

  //      int hash = city + weatherProvider;

        if (!weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).isPresent()
                || weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).get().expired()) {
            new WeatherUpdater(weatherRepository).Update(city, weatherProvider);
        }

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with");

        return weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).get();
    }

    @GetMapping("weather")
    public Weather get(HttpServletResponse response, HttpServletRequest request) {
        if (!weatherRepository.findWeatherByCityAndWeatherProvider("Екатеринбург", "Yandex").isPresent()
                || weatherRepository.findWeatherByCityAndWeatherProvider("Екатеринбург", "Yandex").get().expired()) {
            new WeatherUpdater(weatherRepository).Update("Екатеринбург", "Yandex");
        }

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with");

        //return weatherRepository.findById(11).get();
        return weatherRepository.findWeatherByCityAndWeatherProvider("Екатеринбург", "Yandex").get();
    }

}