package zerobase.projectweather.exception;

import lombok.*;
import zerobase.projectweather.type.ErrorCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public WeatherException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
