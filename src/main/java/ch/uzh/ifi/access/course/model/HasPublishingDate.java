package ch.uzh.ifi.access.course.model;

import java.time.ZonedDateTime;

public interface HasPublishingDate {

    ZonedDateTime getPublishDate();

    default boolean isPublished() {
        return getPublishDate() != null && getPublishDate().isBefore(ZonedDateTime.now());
    }
}
