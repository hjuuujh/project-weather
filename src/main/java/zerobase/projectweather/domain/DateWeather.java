package zerobase.projectweather.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Entity(name = "date_weather")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DateWeather {

    @Id
    private LocalDate date;
    private String weather;
    private String icon;
    private double temperature;
}