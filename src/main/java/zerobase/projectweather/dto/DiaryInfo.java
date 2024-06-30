package zerobase.projectweather.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryInfo {
    private LocalDate date;
    private String weather;
    private String text;
}
