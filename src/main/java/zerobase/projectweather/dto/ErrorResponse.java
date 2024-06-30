package zerobase.projectweather.dto;

import lombok.*;
import zerobase.projectweather.exception.WeatherException;
import zerobase.projectweather.type.ErrorCode;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ErrorResponse {
    private ErrorCode errorCode;
    private String errorMessage;

    public static ErrorResponse from(WeatherException e) {
        return ErrorResponse.builder()
                .errorMessage(e.getErrorMessage())
                .errorCode(e.getErrorCode())
                .build();
    }
}