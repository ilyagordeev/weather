package com.ctest.weather.controller;

import com.ctest.weather.model.WeatherRepository;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Controller
public class IndexController {

    private final WeatherRepository weatherRepository;
    private final LocalLocation localLocation;

    public IndexController(WeatherRepository weatherRepository, LocalLocation localLocation) {
        this.weatherRepository = weatherRepository;
        this.localLocation = localLocation;
    }

    @GetMapping("/")
    public ModelAndView index(HttpServletResponse response, HttpServletRequest request) {
        String city = "Екатеринбург";

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            try {
                String getCity = localLocation.getLocation(request.getRemoteHost());
                if (getCity != null) city = getCity;
            } catch (IOException | GeoIp2Exception e) {
                e.printStackTrace();
            }
        }

        String finalCity = city;
        Map<String, String> model = new HashMap<String, String>(){{
            put("city", finalCity);
        }};

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with");

        return new ModelAndView("index", model);
    }

}