package zerobase.projectweather.dto;

import lombok.*;
import zerobase.projectweather.domain.Diary;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryDto {
    private Long id;

    private String weather;
    private String text;
    private LocalDate date;

    public static DiaryDto from(Diary diary) {
        return DiaryDto.builder()
                .id(diary.getId())
                .weather(diary.getWeather().getWeather())
                .text(diary.getText())
                .date(diary.getWeather().getDate())
                .build();
    }

    public static DiaryDto fromDelete(Diary diary) {
        return DiaryDto.builder()
                .id(diary.getId())
                .date(diary.getDate())
                .build();
    }

}
