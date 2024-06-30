package zerobase.projectweather.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_DATE_FORMAT("날짜 형식이 잘못되었습니다."),
    TEXT_TOO_LONG("일기 내용의 길이가 너무 깁니다."),
    INVALID_DATE_PERIOD("날짜 기간이 잘못되었습니다."),
    DIARY_NOT_FOUND("해당 날짜에 일기가 없습니다."),
    INTERNAL_SERVER_ERROR("내부 서버 오류");

    private final String description;

}
