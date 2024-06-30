package zerobase.projectweather.dto;

import com.sun.istack.NotNull;
import lombok.*;

import java.time.LocalDate;

public class CreateDiary {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {

        @NotNull
        private String date;
        @NotNull
        private String text;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private LocalDate date;
        private String text;
        private String weather;

        public static Response from(DiaryDto diaryDto) {
            return Response.builder()
                    .date(diaryDto.getDate())
                    .text(diaryDto.getText())
                    .weather(diaryDto.getWeather())
                    .build();
        }

    }
}
