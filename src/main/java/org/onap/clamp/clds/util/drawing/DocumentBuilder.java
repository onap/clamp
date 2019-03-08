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

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocumentBuilder {
    private final Document document;
    private final Document documentFactory;

    static final String DATA_ELEMENT_ID_ATTRIBUTE = "data-element-id";

    public DocumentBuilder(Document document, SVGGraphics2D g2d) {
        this.document = document;
        this.documentFactory = g2d.getDOMFactory();
    }
    public DocumentBuilder(Document document, Document documentFactory) {
        this.document = document;
        this.documentFactory = documentFactory;
    }

    void pushChangestoDocument(SVGGraphics2D g2d, String dataElementId) {
        Element element =
            this.documentFactory.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI,
                SVGGraphics2D.SVG_G_TAG);
        element.setAttribute(DATA_ELEMENT_ID_ATTRIBUTE, dataElementId);
        g2d.getRoot(element);
        Node node = this.document.importNode(element, true);
        this.document.getDocumentElement().appendChild(node);
    }

    public Document getDocument() {
        return document;
    }
}
