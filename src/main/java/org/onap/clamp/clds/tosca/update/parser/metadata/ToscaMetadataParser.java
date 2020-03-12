package org.onap.clamp.clds.tosca.update.parser.metadata;

import com.google.gson.JsonObject;
import org.onap.clamp.clds.tosca.update.elements.ToscaElementProperty;

public interface ToscaMetadataParser {
    JsonObject processAllMetadataElement(ToscaElementProperty toscaElementProperty);
}
