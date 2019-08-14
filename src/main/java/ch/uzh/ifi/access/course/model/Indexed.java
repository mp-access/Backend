package ch.uzh.ifi.access.course.model;

public interface Indexed<T> {

    int getIndex();

    void update(T update);

    default boolean hasSameIndex(Indexed other) {
        return other != null && getIndex() == other.getIndex();
    }
}
