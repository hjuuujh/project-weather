package zerobase.projectweather.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import zerobase.projectweather.ProjectWeatherApplication;
import zerobase.projectweather.domain.DateWeather;
import zerobase.projectweather.domain.Diary;
import zerobase.projectweather.dto.DiaryDto;
import zerobase.projectweather.exception.WeatherException;
import zerobase.projectweather.repository.DateWeatherRepository;
import zerobase.projectweather.repository.DiaryRepository;
import zerobase.projectweather.type.ErrorCode;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional // 하위 메소드들 각각 트랜잭션으로 동작 하도록
@RequiredArgsConstructor
@PropertySource("classpath:application-security.properties")
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProjectWeatherApplication.class); // 프로젝트 전체에 로거 하나만 사용

    @Value("${apiKey}")
    private String apiKey;

    @Transactional
    public DiaryDto createDiary(String date, String text) {
        logger.info("Create diary");

        checkDateFormat(date);

        checkTextLength(text);

        // 날씨데이터 DB에서 가져오기
        DateWeather dateWeather = getDateWeather(LocalDate.parse(date));

        // 파싱된 데이터 + 일기 내용 db에 넣기
        Diary diary = Diary.builder()
                .weather(dateWeather)
                .text(text)
                .date(LocalDate.now())
                .build();
        logger.info("Diary created");
        return DiaryDto.from(diaryRepository.save(diary));
    }

    private void checkTextLength(String text) {
        if (text.length() > 255) {
            throw new WeatherException(ErrorCode.TEXT_TOO_LONG);
        }
    }

    private boolean checkDateFormat(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            formatter.setLenient(false);
            Date d = formatter.parse(date);
            return true;
        } catch (java.text.ParseException e) {
            throw new WeatherException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeathers = dateWeatherRepository.findAllByDate(date);
        if (dateWeathers.isEmpty()) {
            return getWeatherFromApi();
        } else {
            return dateWeathers.get(0);
        }
    }

    private DateWeather getWeatherFromApi() {
        // open weather map api 에서 날씨 데이터 가져오기
        String weatherDate = getWeatehrString();

        // 받아온 날씨 json 파싱하기
        Map<String, Object> parseWeather = parseWeather(weatherDate);
        DateWeather dateWeather = DateWeather.builder()
                .date(LocalDate.now())
                .weather(parseWeather.get("main").toString())
                .icon(parseWeather.get("icon").toString())
                .temperature((Double) parseWeather.get("temp"))
                .build();
//        dateWeatherRepository.save(dateWeather);
        return dateWeather;

    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) parser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException("Parsing error", e);
        }

        Map<String, Object> resultMap = new HashMap<>();
        JSONObject main = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", main.get("temp"));

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherObject = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherObject.get("main"));
        resultMap.put("icon", weatherObject.get("icon"));
        return resultMap;

    }

    private String getWeatehrString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    public List<DiaryDto> getDiariesByDate(String date) {
        checkDateFormat(date);
        diaryNotExists(date);

        List<Diary> diaries = diaryRepository.findAllByDate(LocalDate.parse(date));


        return diaries.stream().map(DiaryDto::from).collect(Collectors.toList());
    }

    private void diaryNotExists(String date) {
        if(!diaryRepository.existsByDate(LocalDate.parse(date))){
            throw new WeatherException(ErrorCode.DIARY_NOT_FOUND);
        }
    }


    public List<DiaryDto> getDiariesByDatePeriod(String startDate, String endDate) {
        checkDateFormat(startDate);
        checkDateFormat(endDate);


        if (startDate.compareTo(endDate) > 0) {
            // s1.compareTo(s2) s1이 더크면 음수 같으면 0 작으면 양수
            throw new WeatherException(ErrorCode.INVALID_DATE_PERIOD);
        }

        List<Diary> diaries = diaryRepository.findAllByDateBetween(LocalDate.parse(startDate), LocalDate.parse(endDate));
        System.out.println(LocalDate.parse(startDate) + " " + LocalDate.parse(endDate));
        if (diaries.isEmpty()) {
            throw new WeatherException(ErrorCode.DIARY_NOT_FOUND);
        }
        return diaries.stream().map(DiaryDto::from).collect(Collectors.toList());
    }


    public DiaryDto updateDiary(String date, String text) {
        diaryNotExists(date);
        checkTextLength(text);

        Diary diary = diaryRepository.getFirstByDate(LocalDate.parse(date));


        Diary newDiary = Diary.builder()
                .id(diary.getId())
                .date(diary.getDate())
                .weather(diary.getWeather())
                .text(text)
                .build();

        diaryRepository.save(newDiary);
        return DiaryDto.from(newDiary);
    }

    public DiaryDto deleteDiary(String date) {
        checkDateFormat(date);
        diaryNotExists(date);
        diaryRepository.deleteAllByDate(LocalDate.parse(date));
        return DiaryDto.fromDelete(Diary.builder().date(LocalDate.parse(date)).build());
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        dateWeatherRepository.save(getWeatherFromApi());
    }

}
