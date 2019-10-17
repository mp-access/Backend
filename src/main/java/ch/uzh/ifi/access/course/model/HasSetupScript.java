package ch.uzh.ifi.access.course.model;

import org.springframework.util.StringUtils;

public interface HasSetupScript {

    String getGradingSetup();

    default boolean hasGradingSetupScript() {
        return getGradingSetup() != null && !StringUtils.isEmpty(getGradingSetup());
    }
}
