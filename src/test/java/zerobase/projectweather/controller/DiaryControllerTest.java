package zerobase.projectweather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import zerobase.projectweather.domain.Diary;
import zerobase.projectweather.dto.CreateDiary;
import zerobase.projectweather.dto.DiaryDto;
import zerobase.projectweather.service.DiaryService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

    // mock이 bean으로 등록되어 자동 주입되어있기 때문에 injection 안해도됨
    @MockBean
    private DiaryService diaryService;

    // http 요청을 작성하고 컨트롤러의 응답을 검증할 수 있게 해줌
    @Autowired
    private MockMvc mockMvc;

    // json parsing 위함
    // JSON 컨텐츠를 Java 객체로 deserialization 하거나 Java 객체를 JSON으로 serialization 할 때 사용하는 Jackson 라이브러리의 클래스
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successCreateDiary() throws Exception {
        //given
        given(diaryService.createDiary(any(), anyString()))
                .willReturn(DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .text("안녕")
                        .weather("Clear").build());

        //when

        //then
        mockMvc.perform(post("/create/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateDiary.Request("2024-06-29", "안녕")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2024-06-29"))
                .andExpect(jsonPath("$.text").value("안녕"))
                .andExpect(jsonPath("$.weather").value("Clear"))
                .andDo(print());
    }

    @Test
    void successReadDiary() throws Exception {
        //given
        List<DiaryDto> diaryList = Arrays.asList(
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 흐림")
                        .build(),
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 맑음")
                        .build(),
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 눈")
                        .build()
        );

        given(diaryService.getDiariesByDate(anyString()))
                .willReturn(diaryList);

        //when

        //then
        mockMvc.perform(get("/read/diary?date=2024-06-28"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2024-06-28"))
                .andExpect(jsonPath("$[0].weather").value("Cloud"))
                .andExpect(jsonPath("$[0].text").value("오늘의 날씨는 흐림"))
                .andExpect(jsonPath("$[1].date").value("2024-06-28"))
                .andExpect(jsonPath("$[1].weather").value("Cloud"))
                .andExpect(jsonPath("$[1].text").value("오늘의 날씨는 맑음"))
                .andExpect(jsonPath("$[2].date").value("2024-06-28"))
                .andExpect(jsonPath("$[2].weather").value("Cloud"))
                .andExpect(jsonPath("$[2].text").value("오늘의 날씨는 눈"));
    }

    @Test
    void successReadDiaryByPeriod() throws Exception {
        //given
        List<DiaryDto> diaryList = Arrays.asList(
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 흐림")
                        .build(),
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 맑음")
                        .build(),
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 눈")
                        .build()
        );

        given(diaryService.getDiariesByDatePeriod(anyString(), anyString()))
                .willReturn(diaryList);

        //when

        //then
        mockMvc.perform(get("/read/diaries?startDate=2024-06-28&endDate=2024-06-29"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2024-06-28"))
                .andExpect(jsonPath("$[0].weather").value("Cloud"))
                .andExpect(jsonPath("$[0].text").value("오늘의 날씨는 흐림"))
                .andExpect(jsonPath("$[1].date").value("2024-06-29"))
                .andExpect(jsonPath("$[1].weather").value("Cloud"))
                .andExpect(jsonPath("$[1].text").value("오늘의 날씨는 맑음"))
                .andExpect(jsonPath("$[2].date").value("2024-06-29"))
                .andExpect(jsonPath("$[2].weather").value("Cloud"))
                .andExpect(jsonPath("$[2].text").value("오늘의 날씨는 눈"));
    }

    @Test
    void successUpdateDiary() throws Exception {
        //given
        List<DiaryDto> diaryList = Arrays.asList(
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 흐림")
                        .build(),
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 맑음")
                        .build(),
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 눈")
                        .build()
        );

        diaryList.get(1).setText("다이어리 업데이트");

        given(diaryService.updateDiary(anyString(), anyString()))
                .willReturn(diaryList.get(1));

        //when

        //then
        mockMvc.perform(put("/update/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateDiary.Request("2024-06-29", "다이어리 업데이트")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2024-06-29"))
                .andExpect(jsonPath("$.text").value("다이어리 업데이트"))
                .andExpect(jsonPath("$.weather").value("Cloud"))
                .andDo(print());
    }

    @Test
    void successDeleteDiary() throws Exception {
        //given
        List<DiaryDto> diaryList = Arrays.asList(
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-28"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 흐림")
                        .build(),
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 맑음")
                        .build(),
                DiaryDto.builder()
                        .date(LocalDate.parse("2024-06-29"))
                        .weather("Cloud")
                        .text("오늘의 날씨는 눈")
                        .build()
        );

        given(diaryService.deleteDiary(anyString()))
                .willReturn(DiaryDto.fromDelete(Diary.builder().date(LocalDate.parse("2024-06-29")).build()));

        //when

        //then
        mockMvc.perform(delete("/delete/diary?date=2024-06-29"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2024-06-29"));
        ;
    }
}