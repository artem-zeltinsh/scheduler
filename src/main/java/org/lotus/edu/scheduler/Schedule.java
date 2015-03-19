package org.lotus.edu.scheduler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static java.util.stream.Collectors.toCollection;

public final class Schedule {
    public final Set<Item> items;

    private Schedule(Set<Item> items) {
        this.items = items;
    }

    public static Schedule of(LocalTime openTime, LocalTime closeTime, List<BookingRequest> bookingRequests) {
        return new Schedule(bookingRequests.stream()
                .filter(r -> r.fallsInTimeInterval(openTime, closeTime))
                .sorted((BookingRequest r1, BookingRequest r2) -> r1.submissionTime.compareTo(r2.submissionTime))
                .map(Item::of)
                .collect(toCollection(TreeSet::new)));
    }

    public Set<Item> getItems() {
        return new TreeSet<>(items);
    }

    public Set<Item> getItems(LocalDate date) {
        return items.stream().filter(i -> i.date.equals(date)).collect(toCollection(TreeSet::new));
    }

    public static class Item implements Comparable<Item> {

        public final LocalDate date;
        public final LocalTime startTime;
        public final LocalTime endTime;
        public final String employeeId;

        private Item(LocalDate date, LocalTime startTime, LocalTime endTime, String employeeId) {
            if (endTime.isBefore(startTime)) {
                throw new IllegalArgumentException("start time should be before end time");
            }
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.employeeId = employeeId;
        }

        public static Item of(BookingRequest r) {
            return new Item(r.startTime.toLocalDate(), r.startTime.toLocalTime(), r.endTime.toLocalTime(), r
                    .employeeId);
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
