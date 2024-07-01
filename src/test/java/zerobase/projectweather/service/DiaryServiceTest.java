package zerobase.projectweather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import zerobase.projectweather.domain.DateWeather;
import zerobase.projectweather.domain.Diary;
import zerobase.projectweather.dto.DiaryDto;
import zerobase.projectweather.exception.WeatherException;
import zerobase.projectweather.repository.DateWeatherRepository;
import zerobase.projectweather.repository.DiaryRepository;
import zerobase.projectweather.type.ErrorCode;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private DateWeatherRepository dateWeatherRepository;

    @InjectMocks
    private DiaryService diaryService;


    @BeforeEach
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(diaryService, "apiKey", "33b8906ff78150aa84a81e63caa9e392");
    }


    /**
     * 다이어리 생성
     * 1. 성공 - 날씨 데이터 없는 경우
     * 2. 성공 - 날씨 데이터 있는 경우
     * 2. 실패 - 잘못된 날짜 형식
     * 3. 실패 - 너무 긴 일기 내용
     */
    @Test
    @DisplayName("다이어리 생성 - 성공 - 날씨 데이터 없는 경우")
    void successCreateDiaryNeedWeather() {
        //given

        given(diaryRepository.save(any()))
                .willReturn(Diary.builder()
                        .date(LocalDate.now())
                        .weather(DateWeather.builder()
                                .date(LocalDate.parse("2024-06-30"))
                                .weather("Clouds")
                                .icon("01d")
                                .temperature(303.91)
                                .build())
                        .text("안녕하세요")
                        .build());

        ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);

        //when
        DiaryDto diaryDto = diaryService.createDiary("2024-06-30", "안녕하세요");

        //then
        verify(diaryRepository, times(1)).save(captor.capture()); // save()를 한번 호출 했는지 확인
        assertEquals("Clouds", captor.getValue().getWeather().getWeather());
        assertEquals("안녕하세요", captor.getValue().getText());
        assertEquals("2024-06-30", diaryDto.getDate());
        assertEquals("Clouds", diaryDto.getWeather());
        assertEquals("안녕하세요", diaryDto.getText());
    }

    @Test
    @DisplayName("다이어리 생성 - 성공 - 날씨 데이터 있는 경우")
    void successCreateDiary() {
        //given
        List<DateWeather> dateWeather = Arrays.asList(DateWeather.builder()
                .date(LocalDate.parse("2024-06-30"))
                .weather("Clear")
                .icon("01d")
                .temperature(303.91)
                .build());

        given(dateWeatherRepository.findAllByDate(any()))
                .willReturn(dateWeather);

        given(diaryRepository.save(any()))
                .willReturn(Diary.builder()
                        .date(LocalDate.now())
                        .weather(dateWeather.get(0))
                        .text("안녕하세요")
                        .build());
        ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);

        //when
        DiaryDto diaryDto = diaryService.createDiary("2024-06-30", "안녕하세요");

        //then
        verify(diaryRepository, times(1)).save(captor.capture()); // save()를 한번 호출 했는지 확인
        assertEquals("Clear", captor.getValue().getWeather().getWeather());
        assertEquals("안녕하세요", captor.getValue().getText());
        assertEquals(LocalDate.parse("2024-06-30"), diaryDto.getDate());
        assertEquals(dateWeather.get(0).getWeather(), diaryDto.getWeather());
        assertEquals("안녕하세요", diaryDto.getText());
    }

    @Test
    @DisplayName("다이어리 생성 - 실패 - 잘못된 날짜 형식")
    void failCreateDiary_invalidDateFormat() {
        //given
        DateWeather dateWeather = DateWeather.builder()
                .date(LocalDate.parse("2024-08-10"))
                .weather("Clear")
                .icon("01d")
                .temperature(303.91)
                .build();

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.createDiary("2024/08/10", "안녕하세요"));

        //then
        assertEquals(ErrorCode.INVALID_DATE_FORMAT, exception.getErrorCode());
    }

    @Test
    @DisplayName("다이어리 생성 - 실패 - 너무 긴 일기 내용")
    void failCreateDiary_tooLongText() {
        //given
        DateWeather dateWeather = DateWeather.builder()
                .date(LocalDate.parse("2024-08-10"))
                .weather("Clear")
                .icon("01d")
                .temperature(303.91)
                .build();

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.createDiary("2024-08-10"
                        , "안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 "));

        //then
        assertEquals(ErrorCode.TEXT_TOO_LONG, exception.getErrorCode());

    }

    /**
     * 특정일 일기 가져오기
     * 1. 성공
     * 2. 실패 - 날짜 형식이 잘못된 경우
     * 3. 실패 - 일기가 없는 경우
     */
    @Test
    @DisplayName("특정일 일기 가져오기 - 성공")
    void successReadDiary() {
        //given
        List<DateWeather> dateWeathers = Arrays.asList(
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build(),
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build(),
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-30"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build());
        List<Diary> diaryList = Arrays.asList(
                Diary.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather(dateWeathers.get(0))
                        .text("오늘의 날씨는 흐림")
                        .build(),
                Diary.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather(dateWeathers.get(1))
                        .text("오늘의 날씨는 맑음")
                        .build(),
                Diary.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather(dateWeathers.get(1))
                        .text("오늘의 날씨는 눈")
                        .build(),
                Diary.builder()
                        .date(LocalDate.parse("2024-06-30"))
                        .weather(dateWeathers.get(2))
                        .text("오늘의 날씨는 비")
                        .build()
        );

        given(diaryRepository.findAllByDate(any()))
                .willReturn(diaryList.stream().filter(diary -> diary.getDate().equals(LocalDate.parse("2024-06-29"))).collect(Collectors.toList()));

        //when
        List<DiaryDto> diaryDtos = diaryService.getDiariesByDate("2024-06-29");

        //then
        assertEquals(2, diaryDtos.size());
        assertEquals(LocalDate.parse("2024-06-29"), diaryDtos.get(0).getDate());
        assertEquals(dateWeathers.get(0).getWeather(), diaryDtos.get(0).getWeather());
        assertEquals("오늘의 날씨는 맑음", diaryDtos.get(0).getText());
        assertEquals(LocalDate.parse("2024-06-29"), diaryDtos.get(1).getDate());
        assertEquals(dateWeathers.get(0).getWeather(), diaryDtos.get(1).getWeather());
        assertEquals("오늘의 날씨는 눈", diaryDtos.get(1).getText());
    }

    @Test
    @DisplayName("특정일 일기 가져오기 - 실패 -  날짜 형식이 잘못된 경우")
    void failReadDiary_invalidDateFormat() {

        //given
        List<DateWeather> dateWeathers = Arrays.asList(
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build());

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.getDiariesByDate("2024/06/28"));

        //then
        assertEquals(ErrorCode.INVALID_DATE_FORMAT, exception.getErrorCode());
    }

    @Test
    @DisplayName("특정일 일기 가져오기 - 실패 -  일기가 없는 경우")
    void failReadDiary_diaryNotFound() {
        //given
        given(diaryRepository.findAllByDate(any()))
                .willReturn(Arrays.asList());

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.getDiariesByDate("2020-06-28"));

        //then
        assertEquals(ErrorCode.DIARY_NOT_FOUND, exception.getErrorCode());
    }

    /**
     * 특정 기간의 일기 가져오기
     * 1. 성공
     * 2. 실패 - 날짜 형식이 잘못된 경우
     * 3. 실패 - 날짜 기간이 잘못된 경우
     * 4. 실패 - 일기가 없는 경우
     */
    @Test
    @DisplayName("특정 기간의 일기 가져오기 - 성공")
    void successReadDiaryByPeriod() {
        //given
        List<DateWeather> dateWeathers = Arrays.asList(
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build(),
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build(),
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-30"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build());
        List<Diary> diaryList = Arrays.asList(
                Diary.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather(dateWeathers.get(0))
                        .text("오늘의 날씨는 흐림")
                        .build(),
                Diary.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather(dateWeathers.get(1))
                        .text("오늘의 날씨는 맑음")
                        .build(),
                Diary.builder()
                        .date(LocalDate.parse("2024-06-30"))
                        .weather(dateWeathers.get(2))
                        .text("오늘의 날씨는 비")
                        .build()
        );

        given(diaryRepository.findAllByDateBetween(any(), any()))
                .willReturn(diaryList.stream().filter(diary -> diary.getDate().compareTo(LocalDate.parse("2024-06-28")) >= 0 && diary.getDate().compareTo(LocalDate.parse("2024-06-29")) <= 0).collect(Collectors.toList()));

        //when
        List<DiaryDto> diaryDtos = diaryService.getDiariesByDatePeriod("2024-06-28", "2024-06-29");

        //then
        assertEquals(2, diaryDtos.size());
        assertEquals(LocalDate.parse("2024-06-28"), diaryDtos.get(0).getDate());
        assertEquals(dateWeathers.get(0).getWeather(), diaryDtos.get(0).getWeather());
        assertEquals("오늘의 날씨는 흐림", diaryDtos.get(0).getText());
        assertEquals(LocalDate.parse("2024-06-29"), diaryDtos.get(1).getDate());
        assertEquals(dateWeathers.get(0).getWeather(), diaryDtos.get(1).getWeather());
        assertEquals("오늘의 날씨는 맑음", diaryDtos.get(1).getText());
    }

    @Test
    @DisplayName("특정 기간의 일기 가져오기 - 실패 - 날짜 형식이 잘못된 경우")
    void failReadDiaryByPeriod_invalidDateFormat() {
        //given
        List<DateWeather> dateWeathers = Arrays.asList(
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build());

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.getDiariesByDatePeriod("2024/06/28", "2020-06-29"));

        //then
        assertEquals(ErrorCode.INVALID_DATE_FORMAT, exception.getErrorCode());
    }

    @Test
    @DisplayName("특정 기간의 일기 가져오기 - 실패 - 날짜 기간이 잘못된 경우")
    void failReadDiaryByPeriod_invalidDatePeriod() {
        //given
        List<DateWeather> dateWeathers = Arrays.asList(
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build());

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.getDiariesByDatePeriod("2024-06-30", "2024-06-29"));

        //then
        assertEquals(ErrorCode.INVALID_DATE_PERIOD, exception.getErrorCode());
    }

    @Test
    @DisplayName("특정 기간의 일기 가져오기 - 실패 - 일기가 없는 경우")
    void failReadDiaryByPeriod_diaryNotFound() {
        //given
        given(diaryRepository.findAllByDateBetween(any(), any()))
                .willReturn(Arrays.asList());

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.getDiariesByDatePeriod("2020-06-28", "2020-06-29"));

        //then
        assertEquals(ErrorCode.DIARY_NOT_FOUND, exception.getErrorCode());
    }

    /**
     * 일기 수정
     * 1. 성공
     * 2. 실패 - 일기가 없는 경우
     * 3. 실패 - 일기 길이가 너무 긴 경우
     */
    @Test
    @DisplayName("일기 수정 - 성공")
    void successUpdateDiary() {
        //given
        List<DateWeather> dateWeathers = Arrays.asList(
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build(),
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build(),
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-30"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build());

        List<Diary> diaryList = Arrays.asList(
                Diary.builder()
                        .id(1L)
                        .date(LocalDate.parse("2024-06-28"))
                        .weather(dateWeathers.get(0))
                        .text("오늘의 날씨는 흐림")
                        .build(),
                Diary.builder()
                        .id(2L)
                        .date(LocalDate.parse("2024-06-28"))
                        .weather(dateWeathers.get(0))
                        .text("오늘의 날씨는 맑음")
                        .build(),
                Diary.builder()
                        .id(3L)
                        .date(LocalDate.parse("2024-06-30"))
                        .weather(dateWeathers.get(2))
                        .text("오늘의 날씨는 비")
                        .build()
        );

        given(diaryRepository.getFirstByDate(any()))
                .willReturn(diaryList.stream().filter(diary -> diary.getDate().equals(LocalDate.parse("2024-06-28"))).findFirst().get());

        //when
        DiaryDto newDiary = diaryService.updateDiary("2024-06-28", "업데이트");

        //then
        assertEquals(LocalDate.parse("2024-06-28"), newDiary.getDate());
        assertEquals(dateWeathers.get(0).getWeather(), newDiary.getWeather());
        assertEquals("업데이트", newDiary.getText());
    }

    @Test
    @DisplayName("일기 수정 - 실패 - 일기가 없는 경우")
    void failUpdateDiary_diaryNotFound() {
        //given

        given(diaryRepository.getFirstByDate(any()))
                .willReturn(null);

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.updateDiary("2024-06-28", "업데이트"));

        //then
        assertEquals(ErrorCode.DIARY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("일기 수정 - 실패 - 일기 길이가 너무 긴 경우")
    void failUpdateDiary_textTooLong() {
        //given
        List<DateWeather> dateWeathers = Arrays.asList(
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build(),
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build(),
                DateWeather.builder()
                        .date(LocalDate.parse("2024-06-30"))
                        .weather("Clouds")
                        .icon("01d")
                        .temperature(303.91)
                        .build());

        List<Diary> diaryList = Arrays.asList(
                Diary.builder()
                        .id(1L)
                        .date(LocalDate.parse("2024-06-28"))
                        .weather(dateWeathers.get(0))
                        .text("오늘의 날씨는 흐림")
                        .build(),
                Diary.builder()
                        .id(2L)
                        .date(LocalDate.parse("2024-06-28"))
                        .weather(dateWeathers.get(0))
                        .text("오늘의 날씨는 맑음")
                        .build(),
                Diary.builder()
                        .id(3L)
                        .date(LocalDate.parse("2024-06-30"))
                        .weather(dateWeathers.get(2))
                        .text("오늘의 날씨는 비")
                        .build()
        );

        given(diaryRepository.getFirstByDate(any()))
                .willReturn(diaryList.stream().filter(diary -> diary.getDate().equals(LocalDate.parse("2024-06-28"))).findFirst().get());

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.updateDiary("2024-06-28"
                        , "안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요 "));

        //then
        assertEquals(ErrorCode.TEXT_TOO_LONG, exception.getErrorCode());
    }

    /**
     * 해당 날짜의 모든 일기 삭제
     * 1. 성공
     * 2. 실패 - 해당 날짜에 일기 없음
     * 3. 실패 - 잘못된 날짜 형식
     */
    @Test
    @DisplayName("해당 날짜의 모든 일기 삭제 - 성공")
    void successDeleteDiary() {
        //given

        //when
        diaryRepository.deleteAllByDate(any());

        //then
        verify(diaryRepository, times(1)).deleteAllByDate(any());
    }

    @Test
    @DisplayName("해당 날짜의 모든 일기 삭제 - 실패 - 해당 날짜에 일기 없는 경우")
    void failDeleteDiary_diaryNotFound() {
        //given
        given(diaryRepository.findAllByDate(any()))
                .willReturn(Arrays.asList());
//        diaryRepository.deleteAllByDate(any());

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.deleteDiary("2020-06-28"));

        //then
        assertEquals(ErrorCode.DIARY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("해당 날짜의 모든 일기 삭제 - 실패 - 잘못된 날짜 형식")
    void failDeleteDiary_invalidDate() {
        //given
        diaryRepository.deleteAllByDate(any());

        //when
        WeatherException exception = assertThrows(WeatherException.class
                , () -> diaryService.deleteDiary("2024/06/28"));

        //then
        assertEquals(ErrorCode.INVALID_DATE_FORMAT, exception.getErrorCode());
    }
}