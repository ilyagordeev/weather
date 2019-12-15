package com.ctest.weather.controller;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
public class LocalLocation {
    private DatabaseReader reader;

    {
        try {
            reader = new DatabaseReader.Builder(new File("GeoLite2-City.mmdb")).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLocation(String ip) throws IOException, GeoIp2Exception {
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = reader.city(ipAddress);
        City city = response.getCity();

        return city.getNames().get("ru");
    }
}
