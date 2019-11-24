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
        int city = Integer.parseInt(request.getParameter("city"));
        int weatherProvider = Integer.parseInt(request.getParameter("weatherProvider"));

        int hash = city + weatherProvider;

//        response.addCookie(new Cookie("city", city));
//        response.addCookie(new Cookie("weatherProvider", weatherProvider));

        if (!weatherRepository.findById(hash).isPresent()
                || weatherRepository.findById(hash).get().expired()) {
            new WeatherUpdater(weatherRepository).Update(hash);
        }

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with");

        return weatherRepository.findById(hash).get();
    }

    @GetMapping("weather")
    public Weather get(HttpServletResponse response, HttpServletRequest request) {
        if (!weatherRepository.findById(11).isPresent()
                || weatherRepository.findById(11).get().expired()) {
            new WeatherUpdater(weatherRepository).Update(11);
        }

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with");

        return weatherRepository.findById(11).get();
    }

}