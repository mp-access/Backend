package ch.uzh.ifi.access.course.keycloak;

import org.keycloak.admin.client.CreatedResponseUtil;

import javax.ws.rs.core.Response;

class Utils {

    /**
     * Reads the location header from response and closes.
     *
     * @param response
     * @return location header
     */
    static String getCreatedId(Response response) {
        String createdId = CreatedResponseUtil.getCreatedId(response);
        response.close();
        return createdId;
    }
}
