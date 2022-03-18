package ch.uzh.ifi.access.course.model;

public interface HasSetupScript {

    String getGradingSetup();

    default boolean hasGradingSetupScript() {
        return getGradingSetup() != null && !getGradingSetup().isEmpty();
    }
}
