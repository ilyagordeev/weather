package com.ctest.weather.controller;

import com.ctest.weather.model.WeatherRepository;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


@Controller
public class IndexController {

    private final WeatherRepository weatherRepository;
    private DatabaseReader reader;

    {
        try {
            reader = new DatabaseReader.Builder(new File("GeoLite2-City.mmdb")).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IndexController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @GetMapping("/")
    public ModelAndView index(HttpServletResponse response, HttpServletRequest request) {
        String city = "Екатеринбург";

       Cookie[] cookies = request.getCookies();
       if (cookies == null) {
           try {
               String getCity = getLocation(request.getRemoteHost());
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

    public String getLocation(String ip) throws IOException, GeoIp2Exception {

        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = reader.city(ipAddress);

        City city = response.getCity();

        return city.getNames().get("ru");
    }

}