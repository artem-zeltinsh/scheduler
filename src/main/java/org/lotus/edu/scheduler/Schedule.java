package org.lotus.edu.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collector;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * <p>
 * A {@code Schedule} of booked items for the meeting room.<br>
 * {@code Schedule} instance is constructed from booking requests and organizes them into booked items,
 * so that the following is true:
 * <ul>
 * <li>Booking requests are processed in the chronological order in which they were submitted</li>
 * <li>Booking request falling outside office (openTime, closeTime) interval is skipped</li>
 * <li>Booking request overlapping any of already booked items is skipped</li>
 * </ul>
 * </p>
 * <p>This class is immutable.</p>
 */
public final class Schedule {

    private final List<Item> items;

    private Schedule(List<Item> items) {
        this.items = items;
    }

    /**
     * Creates a new {@code Schedule}.
     * @param openTime office open time
     * @param closeTime office close time
     * @param bookingRequests collection of booking requests.
     * @return the new {@code Schedule}
     */
    public static Schedule of(LocalTime openTime, LocalTime closeTime, Collection<BookingRequest> bookingRequests) {
        Collector<BookingRequest, List<Item>, List<Item>> scheduledItemsCollector = Collector.of(ArrayList::new,
                (List<Item> items, BookingRequest request) -> {
                    Item newItem = Item.of(request);
                    if (items.stream().noneMatch(item -> item.overlap(newItem))) {
                        items.add(newItem);
                    }
                },
                (List<Item> left, List<Item> right) -> {
                    left.addAll(right);
                    return left;
                },
                requests -> requests.stream().sorted(comparing(Item::getStartTime)).collect(toList())
        );
        return new Schedule(bookingRequests.stream()
                .filter(request -> request.fallsInTimeInterval(openTime, closeTime))
                .sorted(comparing(BookingRequest::getSubmissionTime))
                .collect(scheduledItemsCollector));
    }

    /**
     * Iterator over sequence of schedule items.
     * @return iterator over scheduled items
     */
    public Iterator<Item> items() {
        return new ArrayList<>(items).iterator();
    }

    /**
     * Iterator over sequence of scheduled items for the specified date.
     * @param date - schedule date
     * @return iterator over schedule items
     */
    public Iterator<Item> items(LocalDate date) {
        return items.stream().filter(i -> i.date.equals(date)).collect(toList()).iterator();
    }

    public static final class Item {

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

        public boolean overlap(Item other) {
            if (!date.equals(other.date)) return false;
            return !(this.startTime.compareTo(other.endTime) >= 0 || endTime.compareTo(other.startTime) <= 0);
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item other = (Item) o;
            return Objects.equals(date, other.date) &&
                    Objects.equals(startTime, other.startTime) &&
                    Objects.equals(endTime, other.endTime) &&
                    Objects.equals(employeeId, other.employeeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, startTime, endTime, employeeId);
        }
    }
}