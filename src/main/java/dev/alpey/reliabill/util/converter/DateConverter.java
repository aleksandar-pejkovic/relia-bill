package dev.alpey.reliabill.util.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DateConverter implements AttributeConverter<Date, String> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public String convertToDatabaseColumn(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date);
    }

    @Override
    public Date convertToEntityAttribute(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Could not parse date string: " + dateString, e);
        }
    }
}
