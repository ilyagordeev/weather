package com.ctest.weather;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Weather {
    @Id
    private Integer id;

    private final Long UpdateTime = 300000L;
    private String temp;
    private String cloudness;
    private String wind;
    private String pressure;
    private String humidity;
    private String color;
    private String result;
    private Long requestTime;

    public Weather() {
        this.requestTime = new Date().getTime();
        this.result = "ok";
    }

    public boolean expired() {
        return (new Date().getTime() - requestTime) >= UpdateTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCloudness() {
        return cloudness;
    }

    public void setCloudness(String cloudness) {
        this.cloudness = cloudness;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
    }

    public void setTemp(String temp) {
        this.temp = temp;
        Float tempI = Float.parseFloat(temp);
        if (tempI <= 0) {
            this.color = "#273E6A";
        } else if (tempI > 0 && tempI < 15) {
            this.color = "#1F6953";
        } else if (tempI >= 15 && tempI < 25) {
            this.color = "#CA361B";
        } else {
            this.color = "#FF1F1F";
        }
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }


    public String getWind() {
        return wind;
    }

    public String getPressure() {
        return pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public String getTemp() {
        return temp;
    }


}
