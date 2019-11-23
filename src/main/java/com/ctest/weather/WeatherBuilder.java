package com.ctest.weather;

public final class WeatherBuilder {
    private Integer id;
    private String temp;
    private String cloudness;
    private String wind;
    private String pressure;
    private String humidity;

    WeatherBuilder() {
    }

    public static WeatherBuilder aWeather() {
        return new WeatherBuilder();
    }

    public WeatherBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public WeatherBuilder withTemp(String temp) {
        this.temp = temp;
        return this;
    }

    public WeatherBuilder withCloudness(String cloudness) {
        this.cloudness = cloudness;
        return this;
    }

    public WeatherBuilder withWind(String wind) {
        this.wind = wind;
        return this;
    }

    public WeatherBuilder withPressure(String pressure) {
        this.pressure = pressure;
        return this;
    }

    public WeatherBuilder withHumidity(String humidity) {
        this.humidity = humidity;
        return this;
    }

    public Weather build() {
        Weather weather = new Weather();
        weather.setId(id);
        weather.setTemp(temp);
        weather.setCloudness(cloudness);
        weather.setWind(wind);
        weather.setPressure(pressure);
        weather.setHumidity(humidity);
        return weather;
    }
}
