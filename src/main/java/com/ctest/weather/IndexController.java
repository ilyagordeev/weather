package com.ctest.weather;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

    private final WeatherRepository weatherRepository;

    public IndexController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(HttpServletResponse response, HttpServletRequest request) {
        String city = "", weatherProvider = "";

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            city = cookies[0].getValue();
            weatherProvider = cookies[1].getValue();
        }

        System.out.println(city + " " + weatherProvider);

        Map<String, String> model = new HashMap<>();
        if (weatherProvider.equals("1")) {
            model.put("checkedYandex", "checked");
            model.put("checkedOW", "");
        } else {
            model.put("checkedYandex", "");
            model.put("checkedOW", "checked");
        }

        return new ModelAndView("index", model);
    }

}