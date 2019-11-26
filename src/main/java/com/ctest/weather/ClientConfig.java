package com.ctest.weather;

import org.aeonbits.owner.Config;

public interface ClientConfig extends Config {
    String TokenGoogleApi();
    String TokenYandexApi();
    String TokenOpenWeatherApi();
}