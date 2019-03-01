package org.onap.clamp.dao.model;

import com.google.gson.JsonObject;

public interface Policy {

    String getName();

    JsonObject getJsonRepresentation();

}
