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

        String city = request.getParameter("city");
        String weatherProvider = request.getParameter("weatherProvider");
        boolean update = true; // update status, true if success

        // проверка корректности провайдера погоды и при необходимости создание/обновление объекта погоды
        if (weatherProvider.equals("Yandex") || weatherProvider.equals("OpenWeather")) {
            if (!weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).isPresent()) {
                update = new WeatherUpdater(weatherRepository).Update(city, weatherProvider);
            } else if (weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).get().expired()) {
                weatherRepository.delete(weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).get());
                update = new WeatherUpdater(weatherRepository).Update(city, weatherProvider);
            }
        } else update = false;


        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with");

        System.out.println(update);

        // создаем объект ошибки
        if (!update) {
            Weather fail = new WeatherBuilder()
                    .withCity(city)
                    .withWeatherProvider(weatherProvider)
                    .withCloudness("0_0")
                    .withHumidity("-_-")
                    .withPressure("$_$")
                    .withTemp("0")
                    .withWind("0").build();
            fail.setResult("notfound");
            return fail;
        }

        return weatherRepository.findWeatherByCityAndWeatherProvider(city, weatherProvider).get();
    }

}