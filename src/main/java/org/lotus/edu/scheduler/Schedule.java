package org.lotus.edu.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;

public final class Schedule {

    private final Set<Item> items;

    private Schedule(Set<Item> items) {
        this.items = items;
    }

    public static Schedule of(LocalTime openTime, LocalTime closeTime, List<BookingRequest> bookingRequests) {
        return new Schedule(bookingRequests.stream()
                .filter(r -> r.fallsInTimeInterval(openTime, closeTime))
                .sorted(comparing(BookingRequest::getSubmissionTime))
                .map(Item::of).collect(toCollection(TreeSet::new)));
    }

    public Iterator<Item> items() {
        return items.iterator();
    }

    public Iterator<Item> items(LocalDate date) {
        return items.stream().filter(i -> i.date.equals(date)).iterator();
    }

    public static class Item implements Comparable<Item> {

        private final LocalDate date;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final String employeeId;

        private Item(LocalDate date, LocalTime startTime, LocalTime endTime, String employeeId) {
            if (endTime.isBefore(startTime)) {
                throw new IllegalArgumentException("start time should be before end time");
            }
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.employeeId = employeeId;
        }

        public static Item of(BookingRequest request) {
            LocalDateTime startTime = request.getStartTime();
            return new Item(startTime.toLocalDate(), startTime.toLocalTime(), request.getEndTime().toLocalTime(),
                    request.getEmployeeId());
        }

        public LocalDate getDate() {
            return date;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        @Override
        public int compareTo(Item other) {
            if (date.equals(other.date)) {
                return startTime.compareTo(other.startTime);
            }
            return date.compareTo(other.date);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;
            if (!date.equals(item.date)) return false;
            return !(startTime.compareTo(item.endTime) >= 0 || endTime.compareTo(item.startTime) <= 0);
        }

        @Override
        public int hashCode() {
            return date.hashCode();
        }
    }
}
