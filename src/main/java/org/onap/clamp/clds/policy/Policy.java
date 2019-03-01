package org.onap.clamp.clds.policy;

import com.google.gson.JsonObject;

public interface Policy {

    String getName();

    JsonObject getJsonRepresentation();

}
