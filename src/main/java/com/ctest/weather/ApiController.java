package com.ctest.weather;

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

        return weatherRepository.findById(hash).get();
    }

}