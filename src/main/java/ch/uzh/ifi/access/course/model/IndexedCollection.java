package ch.uzh.ifi.access.course.model;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public interface IndexedCollection<T extends Indexed<T>> {

    default void update(List<T> otherItems) {
        List<T> thisItems = this.getIndexedItems();

        thisItems.removeIf(thisItem -> otherItems.stream().noneMatch(otherItem -> otherItem.hasSameIndex(thisItem)));

        otherItems.forEach(otherItem -> {
            Optional<T> first = thisItems.stream().filter(thisItem -> thisItem.hasSameIndex(otherItem)).findFirst();
            first.ifPresentOrElse(thisItem -> thisItem.update(otherItem), () -> thisItems.add(otherItem));
        });

        thisItems.sort(Comparator.comparing(Indexed::getIndex));
    }

    List<T> getIndexedItems();
}
