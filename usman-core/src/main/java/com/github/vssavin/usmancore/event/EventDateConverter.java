package com.github.vssavin.usmancore.event;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Provides converting a string representation of a date into a Date object.
 *
 * @author vssavin on 06.12.2023.
 */
@Component
public class EventDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        return new Date(LocalDateTime.parse(source).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

}
