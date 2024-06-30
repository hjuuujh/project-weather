package zerobase.projectweather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import zerobase.projectweather.dto.CreateDiary;
import zerobase.projectweather.dto.DeleteDiary;
import zerobase.projectweather.dto.DiaryDto;
import zerobase.projectweather.dto.DiaryInfo;
import zerobase.projectweather.service.DiaryService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @ApiOperation(value = "날씨 일기 생성", notes = "날짜와 일기 내용을 작성해주세요")
    @PostMapping("/create/diary")
    public CreateDiary.Response createDiary(@RequestBody @Valid CreateDiary.Request request) {
        return CreateDiary.Response.from(diaryService.createDiary(request.getDate(), request.getText()));
    }

    @ApiOperation(value = "날씨 일기 특정 날짜 읽어오기", notes = "보고 싶은 일기들의 날짜를 입력해주세요")
    @GetMapping("/read/diary")
    public List<DiaryInfo> readDiary(@RequestParam("date") @ApiParam(value = "날짜 형식 : yyyy-MM-dd", example = "2024-06-29") String date) {
        List<DiaryDto> diaryInfos = diaryService.getDiariesByDate(date);
        return diaryInfos.stream().map(diaryDto ->
                        DiaryInfo.builder()
                                .date(diaryDto.getDate())
                                .weather(diaryDto.getWeather())
                                .text(diaryDto.getText())
                                .build())
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "날씨 일기 기간 내 읽어오기", notes = "보고 싶은 일기의 기간중 시작날짜와 끝날짜를 입력해주세요")
    @GetMapping("/read/diaries")
    public List<DiaryInfo> readDiaries(@RequestParam("startDate") @ApiParam(value = "조회할 기간의 첫번째 날 \n 날짜 형식 : yyyy-MM-dd", example = "2024-06-29") String startDate,
                                       @RequestParam("endDate") @ApiParam(value = "조회할 기간의 마지막 날 \n날짜 형식 : yyyy-MM-dd", example = "2024-06-29") String endDate) {
        List<DiaryDto> diaryInfos = diaryService.getDiariesByDatePeriod(startDate, endDate);
        return diaryInfos.stream().map(diaryDto ->
                        DiaryInfo.builder()
                                .date(diaryDto.getDate())
                                .weather(diaryDto.getWeather())
                                .text(diaryDto.getText())
                                .build())
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "날씨 일기 수정", notes = "수정하고 싶은 일기의 날짜와 수정할 내용을 입력해주세요 \n 해당 날짜의 첫번째 일기의 내용이 수정됩니다.")
    @PutMapping("/update/diary")
    public CreateDiary.Response updateDiary(@RequestBody @Valid CreateDiary.Request request) {
        return CreateDiary.Response.from(diaryService.updateDiary(request.getDate(), request.getText()));

    }

    @ApiOperation(value = "날씨 일기 삭제", notes = "삭제하고 싶은 일기의 날짜를 입력해주세요 \n 해당 날짜의 일기를 모두 삭제합니다.")
    @DeleteMapping(value = "/delete/diary")
    public DeleteDiary.Response deleteDiary(@RequestParam("date") String date) {
        return DeleteDiary.Response.from(diaryService.deleteDiary(date));
    }

}
