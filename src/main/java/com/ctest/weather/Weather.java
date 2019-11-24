package com.ctest.weather;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Weather {
    @Id
    private Integer id;

    private final Long UpdateTime = 200000L;
    private String temp;
    private String cloudness;
    private String wind;
    private String pressure;
    private String humidity;
    private String color;
    private String color2;
    private String result;
    private Long requestTime;

    public Weather() {
        this.requestTime = new Date().getTime();
        this.result = "ok";
    }

    public boolean expired() {
        return (new Date().getTime() - requestTime) >= UpdateTime;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
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
            this.color = "#74a3ff";
            this.color2 = "#0055ff";
        } else if (tempI > 0 && tempI < 15) {
            this.color = "#52ffcb";
            this.color2 = "#00a070";
        } else if (tempI >= 15 && tempI < 25) {
            this.color = "#ff8e79";
            this.color2 = "#d0280a";
        } else {
            this.color = "#ff0000";
            this.color2 = "#bc0000";
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
