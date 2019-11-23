package com.ctest.weather;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WeatherRepository extends CrudRepository<Weather, Integer> {
}