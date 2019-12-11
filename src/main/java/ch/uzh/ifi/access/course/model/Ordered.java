package ch.uzh.ifi.access.course.model;

public interface Ordered<T> {

    int getOrder();

    void update(T update);

    default boolean hasSameOrder(Ordered other) {
        return other != null && getOrder() == other.getOrder();
    }
}
