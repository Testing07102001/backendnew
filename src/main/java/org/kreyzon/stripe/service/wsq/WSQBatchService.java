package org.kreyzon.stripe.service.wsq;

import org.kreyzon.stripe.dto.WSQ.DayRequestDTO;
import org.kreyzon.stripe.dto.WSQ.SessionRequestDTO;
import org.kreyzon.stripe.dto.WSQ.WSQBatchRequestDTO;
import org.kreyzon.stripe.entity.wsq.*;
import org.kreyzon.stripe.repository.wsq.WSQBatchRepository;
import org.kreyzon.stripe.repository.wsq.WSQCourseRepository;
import org.kreyzon.stripe.repository.wsq.WSQFacilitatorRepository;
import org.kreyzon.stripe.repository.wsq.WSQVenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class WSQBatchService {
    @Autowired
    private WSQBatchRepository batchRepository;

    @Autowired
    private WSQCourseRepository courseRepository;

    @Autowired
    private WSQFacilitatorRepository facilitatorRepository;

    @Autowired
    private WSQVenueRepository venueRepository;

    public WSQBatch createBatch(Long courseId,Long venueId, WSQBatchRequestDTO batchRequestDTO) {
        // Fetch course
        WSQCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        // Fetch facilitators


        // Fetch venue
        WSQVenue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + venueId));

        // Set the course, facilitators, assessors, and venue
        WSQBatch batch = new WSQBatch();
        batch.setCourse(course);
        batch.setVenue(venue);
        batch.setName(batchRequestDTO.getName());
        batch.setIntakeSize(batchRequestDTO.getIntakeSize());

        // Set the batch days with sessions
        if (batchRequestDTO.getDays() != null) {
            Set<LocalDate> uniqueDays = new HashSet<>();
            for (DayRequestDTO dayRequest : batchRequestDTO.getDays()) { // Iterate over the days directly
                LocalDate day = dayRequest.getDate();

                // Check for duplicates
                if (!uniqueDays.add(day)) {
                    throw new RuntimeException("Duplicate date found: " + day);
                }

                WSQBatchDay batchDay = new WSQBatchDay();
                batchDay.setDate(day);

                // Set the reference to the batch
                batchDay.setBatch(batch);  // Ensure batch is set on batchDay

                // Add sessions to the batch day
                List<SessionRequestDTO> sessionRequests = dayRequest.getSessions(); // Get sessions from the day request
                if (sessionRequests != null) {
                    for (SessionRequestDTO sessionRequest : sessionRequests) {
                        WSQSession session = new WSQSession();

                        // Set the session details
                        session.setSession(sessionRequest.getSession());  // Set the session name here
                        session.setStartTime(sessionRequest.getStartTime());
                        session.setEndTime(sessionRequest.getEndTime());

                        batchDay.getSession().add(session); // Add the session to the batch day
                    }
                }

                batch.getDays().add(batchDay); // Add the day to the batch
            }
        }

        return batchRepository.save(batch);
    }

    public WSQBatch getBatchById(Long batchId) {
        return batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));
    }
    public List<WSQBatch> getAllBatches() {
        return batchRepository.findAll();
    }


}
