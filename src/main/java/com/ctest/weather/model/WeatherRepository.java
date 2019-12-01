package com.ctest.weather.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherRepository extends CrudRepository<Weather, Long> {

    List<Weather> findWeatherByCityAndWeatherProvider (String city, String weatherProvider);

}