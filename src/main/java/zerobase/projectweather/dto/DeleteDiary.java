package zerobase.projectweather.dto;

import com.sun.istack.NotNull;
import lombok.*;

import java.time.LocalDate;

public class DeleteDiary {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {

        @NotNull
        private LocalDate date;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private LocalDate date;

        public static DeleteDiary.Response from(DiaryDto diaryDto) {
            return DeleteDiary.Response.builder()
                    .date(diaryDto.getDate())
                    .build();
        }

    }
}
