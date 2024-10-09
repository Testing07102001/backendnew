package org.kreyzon.stripe.service;

import org.kreyzon.stripe.entity.ScheduleDateDetail;
import org.kreyzon.stripe.entity.TimeTable;
import org.kreyzon.stripe.repository.TimeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TimeTableService {
    @Autowired
    private TimeTableRepository timeTableRepository;

    public Optional<TimeTable> getTimeTableById(Long id) {
        return timeTableRepository.findById(id);
    }

    public TimeTable saveTimeTable(TimeTable timeTable) {
        return timeTableRepository.save(timeTable);
    }

    public Optional<TimeTable> getTimeTableByIntakeIdAndBatchId(String intakeId, Long batchId) {
        return timeTableRepository.findByIntakeIdAndBatchId(intakeId, batchId);
    }

    public Optional<TimeTable> updateScheduleDetails(Long timeTableId, Long scheduleDetailId, ScheduleDateDetail updatedDetails) {
        Optional<TimeTable> optionalTimeTable = timeTableRepository.findById(timeTableId);

        if (optionalTimeTable.isPresent()) {
            TimeTable timeTable = optionalTimeTable.get();

            // Find the specific ScheduleDateDetail by ID
            timeTable.getScheduleDateDetails().forEach(scheduleDetail -> {
                if (scheduleDetail.getId().equals(scheduleDetailId)) {
                    scheduleDetail.setStartDate(updatedDetails.getStartDate());
                    scheduleDetail.setEndDate(updatedDetails.getEndDate());
                    scheduleDetail.setDays(updatedDetails.getDays());
                    scheduleDetail.setStartTime(updatedDetails.getStartTime());
                    scheduleDetail.setEndTime(updatedDetails.getEndTime());
                }
            });

            timeTableRepository.save(timeTable);
            return Optional.of(timeTable);
        }
        return Optional.empty();
    }


}
