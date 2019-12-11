package ch.uzh.ifi.access.course.model;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public interface OrderedCollection<T extends Ordered<T>> {

    default void update(List<T> otherItems) {
        List<T> thisItems = this.getOrderedItems();

        thisItems.removeIf(thisItem -> otherItems.stream().noneMatch(otherItem -> otherItem.hasSameOrder(thisItem)));

        otherItems.forEach(otherItem -> {
            Optional<T> first = thisItems.stream().filter(thisItem -> thisItem.hasSameOrder(otherItem)).findFirst();
            first.ifPresentOrElse(thisItem -> thisItem.update(otherItem), () -> thisItems.add(otherItem));
        });

        thisItems.sort(Comparator.comparing(Ordered::getOrder));
    }

    List<T> getOrderedItems();
}
