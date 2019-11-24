package com.ctest.weather;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface WeatherRepository extends CrudRepository<Weather, Long> {

    Optional<Weather> findWeatherByCityAndWeatherProvider (String city, String weatherProvider);

}