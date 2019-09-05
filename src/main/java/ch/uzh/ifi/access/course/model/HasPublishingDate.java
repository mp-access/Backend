package ch.uzh.ifi.access.course.model;

import java.time.LocalDateTime;

public interface HasPublishingDate {

    LocalDateTime getPublishDate();

    default boolean isPublished() {
        return getPublishDate() != null && getPublishDate().isBefore(LocalDateTime.now());
    }
}
