package ch.uzh.ifi.access.course.model;

import java.time.ZonedDateTime;

public interface HasDueDate {

    ZonedDateTime getDueDate();

    default boolean isPastDueDate() {
        return getDueDate() != null && ZonedDateTime.now().isAfter(this.getDueDate());
    }
}
