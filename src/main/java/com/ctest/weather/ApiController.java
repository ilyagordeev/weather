package com.ctest.weather;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
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
        System.out.println(city);
        System.out.println(weatherProvider);
        response.addCookie(new Cookie("city", city));
        response.addCookie(new Cookie("weatherProvider", weatherProvider));

        return weatherRepository.findById(11).get();
    }

    @GetMapping ("/weather")
    public Weather getWeatherGet() {
        System.out.println(weatherRepository.findById(11).get().expired());
        return weatherRepository.findById(11).get();
    }
}