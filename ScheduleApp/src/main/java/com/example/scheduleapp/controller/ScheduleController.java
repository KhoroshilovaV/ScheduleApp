package com.example.scheduleapp.controller;

import com.example.scheduleapp.model.Day;
import com.example.scheduleapp.model.DayRecordLink;
import com.example.scheduleapp.model.Record;
import com.example.scheduleapp.repository.DayRecordLinkRepository;
import com.example.scheduleapp.repository.DayRepository;
import com.example.scheduleapp.repository.RecordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ScheduleController {

    private final DayRepository dayRepository;
    private final RecordRepository recordRepository;
    private final DayRecordLinkRepository dayRecordLinkRepository;

    public ScheduleController(DayRepository dayRepository, RecordRepository recordRepository, DayRecordLinkRepository dayRecordLinkRepository) {
        this.dayRepository = dayRepository;
        this.recordRepository = recordRepository;
        this.dayRecordLinkRepository = dayRecordLinkRepository;
    }

    @GetMapping("/")
    public String schedule(Model model) {
        // Получаем все дни с их записями, отсортированными по времени начала
        List<Day> days = dayRepository.findAll().stream()
                .peek(day -> day.setDayRecordLinks(
                        day.getDayRecordLinks().stream()
                                .sorted((link1, link2) -> link1.getRecord().getStartTime().compareTo(link2.getRecord().getStartTime()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        model.addAttribute("days", days);
        return "schedule"; // Название HTML-шаблона
    }

    @PostMapping("/save")
    @ResponseBody
    public String saveSchedule(@RequestBody Map<Long, List<Map<String, String>>> scheduleData) {
        try {
            for (Map.Entry<Long, List<Map<String, String>>> entry : scheduleData.entrySet()) {
                Long dayId = entry.getKey();
                Day day = dayRepository.findById(dayId).orElseThrow(() -> new RuntimeException("Day not found"));

                for (Map<String, String> recordData : entry.getValue()) {
                    Record record = new Record();
                    record.setStartTime(LocalTime.parse(recordData.get("startTime")));
                    record.setEndTime(LocalTime.parse(recordData.get("endTime")));
                    record.setSubject(recordData.get("subject"));
                    record.setMode(recordData.get("mode").equals("offline") ? "Офлайн" : "Онлайн"); // Преобразуем значение
                    record.setDate(LocalDate.now()); // Установите текущую дату или подходящее значение

                    recordRepository.save(record);

                    DayRecordLink link = new DayRecordLink();
                    link.setDay(day);
                    link.setRecord(record);
                    dayRecordLinkRepository.save(link);
                }
            }
            return "Данные успешно сохранены!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка сохранения данных!";
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public String deleteRecord(@RequestParam Long recordId) {
        try {
            if (recordRepository.existsById(recordId)) {
                recordRepository.deleteById(recordId);
                return "Запись успешно удалена!";
            } else {
                return "Запись не найдена!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка удаления записи!";
        }
    }


}