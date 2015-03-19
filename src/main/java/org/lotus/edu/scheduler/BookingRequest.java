package org.lotus.edu.scheduler;

import java.time.LocalDateTime;
import java.time.LocalTime;

public final class BookingRequest {
    
    public final LocalDateTime submissionTime;
    public final LocalDateTime startTime;
    public final LocalDateTime endTime;
    public final String employeeId;

    public BookingRequest(LocalDateTime submissionTime, LocalDateTime startTime, LocalDateTime endTime, String employeeId) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("start time should be before end time");
        }

        this.submissionTime = submissionTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.employeeId = employeeId;
    }

    public boolean fallsInTimeInterval(LocalTime openTime, LocalTime closeTime) {
        return startTime.toLocalTime().compareTo(openTime) >= 0 && endTime.toLocalTime().compareTo(closeTime) <= 0;
    }
}
