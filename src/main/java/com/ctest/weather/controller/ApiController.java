package com.ctest.weather.controller;

import com.ctest.weather.model.Weather;
import com.ctest.weather.model.WeatherRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class ApiController {

    final WeatherRepository weatherRepository;
    final WeatherUpdater weatherUpdater;
    Weather fail;

    public ApiController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
        weatherUpdater = new WeatherUpdater(weatherRepository);

        fail = new Weather();
    }

    @PostMapping("/weather")
    public Weather getWeather(HttpServletResponse response, HttpServletRequest request) {
        String city = request.getParameter("city");
        String weatherProvider = request.getParameter("weatherProvider");
        fail.setCity(city);
        fail.setWeatherProvider(weatherProvider);
        fail.setResult("notfound");
        boolean update = true; // update status, true if success

        // проверка корректности провайдера погоды и при необходимости создание/обновление объекта погоды
        if (weatherProvider.equals("Yandex") || weatherProvider.equals("OpenWeather")) {
            if (weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).isEmpty()) {
                if (!WeatherUpdater.inUnknownCity(city))
                    update = weatherUpdater.Update(city, weatherProvider);
                else {
                    update = false;
                    fail.setResult("wait");
                }
            } else if (weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).get(0).expired()) {
                weatherRepository.delete(weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).get(0));
                update = weatherUpdater.Update(city, weatherProvider);
            }
        } else update = false;

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with");

        System.out.println(update);
        System.out.println(city);

        // возвращаем ошибку
        if (!update) return fail;

        // возвращаем результат
        return weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).get(0);
    }

}