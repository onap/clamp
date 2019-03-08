/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 Nokia. All rights
 *                             reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */

package org.onap.clamp.clds.util.drawing;

import java.util.Objects;
import org.onap.clamp.clds.util.XmlTools;

public class ClampGraph {
    private final DocumentBuilder documentBuilder;
    private String svg;

    ClampGraph(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    public String getAsSVG() {
        if(Objects.isNull(svg) || svg.isEmpty()) {
            svg = XmlTools.exportXmlDocumentAsString(this.documentBuilder.getDocument());
        }
        return svg;
    }
}
