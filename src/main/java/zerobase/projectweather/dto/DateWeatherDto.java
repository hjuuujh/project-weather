package zerobase.projectweather.dto;

import lombok.*;
import zerobase.projectweather.domain.DateWeather;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateWeatherDto {
    private LocalDate date;
    private String weather;
    private String icon;
    private double temperature;

    public static DateWeatherDto from(DateWeather dateWeather) {
        return DateWeatherDto.builder()
                .date(dateWeather.getDate())
                .weather(dateWeather.getWeather())
                .icon(dateWeather.getIcon())
                .temperature(dateWeather.getTemperature())
                .build();
    }
}
