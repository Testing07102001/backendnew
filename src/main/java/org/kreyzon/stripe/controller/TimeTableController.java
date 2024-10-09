package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.ScheduleDateDetail;
import org.kreyzon.stripe.entity.TimeTable;
import org.kreyzon.stripe.service.TimeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/timetable")
public class TimeTableController {

    @Autowired
    private TimeTableService timeTableService;

    @GetMapping("/{id}")
    public ResponseEntity<TimeTable> getTimeTableById(@PathVariable Long id) {
        Optional<TimeTable> timeTable = timeTableService.getTimeTableById(id);
        return timeTable.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeTable> updateTimeTable(@PathVariable Long id, @RequestBody TimeTable updatedTimeTable) {
        Optional<TimeTable> existingTimeTable = timeTableService.getTimeTableById(id);

        if (existingTimeTable.isPresent()) {
            TimeTable timeTable = existingTimeTable.get();

            // Update fields
            timeTable.setIntakeId(updatedTimeTable.getIntakeId());
            timeTable.setBatchId(updatedTimeTable.getBatchId());
            timeTable.setCourse(updatedTimeTable.getCourse());
            timeTable.setType(updatedTimeTable.getType());
            timeTable.setScheduleDateDetails(updatedTimeTable.getScheduleDateDetails());
            timeTable.setModuleDetails(updatedTimeTable.getModuleDetails());

            TimeTable savedTimeTable = timeTableService.saveTimeTable(timeTable);
            return ResponseEntity.ok(savedTimeTable);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<TimeTable> getTimeTableByIntakeIdAndBatchId(@RequestParam String intakeId, @RequestParam Long batchId) {
        Optional<TimeTable> timeTable = timeTableService.getTimeTableByIntakeIdAndBatchId(intakeId, batchId);
        return timeTable.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{timeTableId}/schedule/{scheduleDetailId}")
    public ResponseEntity<TimeTable> updateScheduleDetails(
            @PathVariable Long timeTableId,
            @PathVariable Long scheduleDetailId,
            @RequestBody ScheduleDateDetail updatedDetails) {

        Optional<TimeTable> updatedTimeTable = timeTableService.updateScheduleDetails(timeTableId, scheduleDetailId, updatedDetails);

        return updatedTimeTable.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


}
