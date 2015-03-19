package org.lotus.edu.scheduler;

import org.junit.Test;
import org.lotus.edu.scheduler.Schedule.Item;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class ScheduleTest {

    @Test
    public void bookingRequestStartTimeIsBeforeOfficeOpenTime() {
        // Given office open and close time
        // and booking request start time is before office open time
        BookingRequest request = createRequest("2011-03-17T10:17:06", "2011-03-21T08:45", "2011-03-21T09:15", "Peter");

        // when scheduling this booking request
        Schedule schedule = Schedule.of(LocalTime.parse("09:00"), LocalTime.parse("19:30"), asList(request));

        // then the booking is not added to the schedule
        assertEmptySchedule(schedule);
    }

    @Test
    public void bookingRequestStartTimeEqualsToOfficeStartTime() {
        // Given office open and close time
        // and booking request start time is equal to office open time
        BookingRequest request = createRequest("2011-03-17T10:19:24", "2011-03-21T09:00", "2011-03-21T09:15", "Mark");

        // when scheduling this booking request
        Schedule schedule = Schedule.of(LocalTime.parse("09:00"), LocalTime.parse("19:30"), asList(request));

        // then the booking is added to the schedule
        assertSchedule(schedule, Item.of(request));
    }

    @Test
    public void bookingRequestEndTimeIsAfterOfficeCloseTime() {
        // Given office working hours
        // and booking request end time is after office close time
        BookingRequest request = createRequest("2011-03-17T10:20:54", "2011-03-21T19:20", "2011-03-21T20:20", "Ann");

        // when scheduling these booking requests
        Schedule schedule = Schedule.of(LocalTime.parse("09:00"), LocalTime.parse("19:30"), asList(request));

        // then the booking is not added to the schedule
        assertEmptySchedule(schedule);
    }

    @Test
    public void bookingRequestEndTimeEqualsToOfficeCloseTime() {
        // Given office working hours
        // and booking request end time falling outside working hours
        BookingRequest request = createRequest("2011-03-17T10:22:14", "2011-03-21T19:20", "2011-03-21T19:30", "Jon");

        // when scheduling these booking requests
        Schedule schedule = Schedule.of(LocalTime.parse("09:00"), LocalTime.parse("19:30"), asList(request));

        // then the booking is added to the schedule
        assertSchedule(schedule, Item.of(request));
    }

    @Test
    public void overlappedBookingRequests() {
        // Given two overlapped booking requests
        BookingRequest request1 = createRequest("2011-03-17T10:18:11", "2011-03-21T09:00", "2011-03-21T11:00", "Peter");
        BookingRequest request2 = createRequest("2011-03-16T12:34:56", "2011-03-21T09:00", "2011-03-21T11:00", "Mark");

        // when scheduling these booking requests
        Schedule schedule = Schedule.of(LocalTime.parse("09:00"), LocalTime.parse("19:30"), asList(request1, request2));

        // then the booking with earlier submission date is added to the schedule
        assertSchedule(schedule, Item.of(request2));
    }

    @Test
    public void sequentialBookingRequests() {
        // Given three sequential booking requests
        BookingRequest r1 = createRequest("2011-03-17T11:15:43", "2011-03-21T09:00", "2011-03-21T11:00", "Ann");
        BookingRequest r2 = createRequest("2011-03-16T12:21:05", "2011-03-21T11:00", "2011-03-21T14:00", "Jon");
        BookingRequest r3 = createRequest("2011-03-16T12:27:14", "2011-03-21T14:00", "2011-03-21T19:30", "Rob");

        // when scheduling these booking requests
        Schedule schedule = Schedule.of(LocalTime.parse("09:00"), LocalTime.parse("19:30"), asList(r1, r2, r3));

        // then all bookings are added to the schedule
        assertScheduleDay(schedule, LocalDate.parse("2011-03-21"), Item.of(r1), Item.of(r2), Item.of(r3));
    }

    private BookingRequest createRequest(String submissionDateTime, String startDateTime, String endDateTime,
                                         String employeeId) {
        return new BookingRequest(LocalDateTime.parse(submissionDateTime),
                LocalDateTime.parse(startDateTime), LocalDateTime.parse(endDateTime), employeeId);
    }

    private static void assertSchedule(Schedule schedule, Item... expectedItems) {
        assertArrayEquals(expectedItems, schedule.getItems().toArray());
    }

    private static void assertScheduleDay(Schedule schedule, LocalDate date, Item... expectedItems) {
        assertArrayEquals(expectedItems, schedule.getItems(date).toArray());
    }

    private static void assertEmptySchedule(Schedule schedule) {
        assertEquals(0, schedule.getItems().size());
    }
}