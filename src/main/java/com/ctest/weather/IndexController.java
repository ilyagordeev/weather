package com.ctest.weather;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

    private final WeatherRepository weatherRepository;

    public IndexController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @GetMapping("/")
    public ModelAndView index(HttpServletRequest request) {
        String city = "10", weatherProvider = "1";

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            city = cookies[0].getValue();
            weatherProvider = cookies[1].getValue();
        }

        Map<String, String> model = new HashMap<>();
        if (weatherProvider.equals("1")) {
            model.put("checkedYandex", "checked");
            model.put("checkedOW", "");
        } else {
            model.put("checkedYandex", "");
            model.put("checkedOW", "checked");
        }
        if (city.equals("10")) {
            model.put("selectedEKB", "selected");
            model.put("selectedMSC", "");
            model.put("selectedNY", "");
            model.put("selectedAM", "");
            model.put("selectedMD", "");
        }
        if (city.equals("20")) {
            model.put("selectedEKB", "");
            model.put("selectedMSC", "selected");
            model.put("selectedNY", "");
            model.put("selectedAM", "");
            model.put("selectedMD", "");
        }
        if (city.equals("30")) {
            model.put("selectedEKB", "");
            model.put("selectedMSC", "");
            model.put("selectedNY", "selected");
            model.put("selectedAM", "");
            model.put("selectedMD", "");
        }
        if (city.equals("40")) {
            model.put("selectedEKB", "");
            model.put("selectedMSC", "");
            model.put("selectedNY", "");
            model.put("selectedAM", "selected");
            model.put("selectedMD", "");
        }
        if (city.equals("50")) {
            model.put("selectedEKB", "");
            model.put("selectedMSC", "");
            model.put("selectedNY", "");
            model.put("selectedAM", "");
            model.put("selectedMD", "selected");
        }

        return new ModelAndView("index", model);
    }

}